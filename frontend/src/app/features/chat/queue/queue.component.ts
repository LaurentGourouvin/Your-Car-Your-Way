import { Component, inject, signal, OnInit, OnDestroy } from '@angular/core';
import { AuthService } from '../../../core/services/auth.service';
import { ChatService } from '../../../core/services/chat.service';
import { WebSocketService } from '../../../core/services/websocket.service';
import { ConversationResponse } from '../../../shared/models/conversation.model';
import { MessageResponse } from '../../../shared/models/message.model';
import { Subscription } from 'rxjs';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-queue',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './queue.component.html',
})
export class QueueComponent implements OnInit, OnDestroy {
  private authService = inject(AuthService);
  private chatService = inject(ChatService);
  private wsService = inject(WebSocketService);

  queue = signal<ConversationResponse[]>([]);
  activeConversation = signal<ConversationResponse | null>(null);
  messages = signal<MessageResponse[]>([]);
  messageContent = signal('');
  private subscriptions: Subscription[] = [];

  ngOnInit() {
    this.loadQueue();

    const queueSub = this.wsService.queueUpdates$.subscribe(() => {
      this.loadQueue();
    });

    const msgSub = this.wsService.messages$.subscribe((msg) => {
      this.messages.update((msgs) => [...msgs, msg]);
    });

    this.subscriptions.push(queueSub, msgSub);

    this.wsService.connect(() => {
      this.wsService.subscribeToQueue();

      this.chatService.getActiveConversation().subscribe({
        next: (conv) => {
          if (conv) {
            this.activeConversation.set(conv);
            this.wsService.subscribeToConversation(conv.id);
            this.chatService.getMessages(conv.id).subscribe({
              next: (msgs) => this.messages.set(msgs),
            });
          }
        },
      });
    });
  }

  loadQueue() {
    this.chatService.getQueue().subscribe({
      next: (queue) => this.queue.set(queue),
    });
  }

  takeConversation(id: string) {
    this.chatService.takeConversation(id).subscribe({
      next: (conv) => {
        this.activeConversation.set(conv);
        this.wsService.subscribeToConversation(conv.id);
        this.loadQueue();

        this.chatService.getMessages(conv.id).subscribe({
          next: (msgs) => this.messages.set(msgs),
        });
      },
    });
  }

  closeConversation() {
    const conv = this.activeConversation();
    if (!conv) return;

    this.chatService.closeConversation(conv.id).subscribe({
      next: () => {
        this.activeConversation.set(null);
        this.messages.set([]);
        this.loadQueue();
      },
    });
  }

  sendMessage() {
    const content = this.messageContent().trim();
    const conv = this.activeConversation();
    if (!content || !conv) return;

    this.wsService.sendMessage(conv.id, content);
    this.messageContent.set('');
  }

  logout() {
    this.wsService.disconnect();
    this.authService.logout();
    window.location.href = '/login';
  }

  ngOnDestroy() {
    this.subscriptions.forEach((s) => s.unsubscribe());
    this.wsService.disconnect();
  }
}
