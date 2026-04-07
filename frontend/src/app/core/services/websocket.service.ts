import { Injectable, inject } from '@angular/core';
import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { AuthService } from './auth.service';
import { MessageResponse } from '../../shared/models/message.model';
import { Subject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class WebSocketService {
  private authService = inject(AuthService);
  private client: Client | null = null;

  private messageSubject = new Subject<MessageResponse>();
  private queueSubject = new Subject<void>();

  messages$ = this.messageSubject.asObservable();
  queueUpdates$ = this.queueSubject.asObservable();

  connect(onConnected?: () => void) {
    this.client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      connectHeaders: {
        Authorization: `Bearer ${this.authService.getToken()}`,
      },
      onConnect: () => {
        console.log('WebSocket connected');
        onConnected?.();
      },
      onDisconnect: () => {
        console.log('WebSocket disconnected');
      },
    });

    this.client.activate();
  }

  subscribeToConversation(conversationId: string) {
    this.client?.subscribe(`/topic/conversation/${conversationId}`, (message: IMessage) => {
      const msg: MessageResponse = JSON.parse(message.body);
      this.messageSubject.next(msg);
    });
  }

  subscribeToQueue() {
    this.client?.subscribe('/topic/queue', () => {
      this.queueSubject.next();
    });
  }

  sendMessage(conversationId: string, content: string) {
    this.client?.publish({
      destination: `/app/chat/${conversationId}`,
      body: JSON.stringify({ content }),
    });
  }

  disconnect() {
    this.client?.deactivate();
    this.client = null;
  }
}
