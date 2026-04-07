import { Component, inject, signal, OnInit, OnDestroy } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { ChatService } from '../../../core/services/chat.service';
import { WebSocketService } from '../../../core/services/websocket.service';
import { ConversationResponse } from '../../../shared/models/conversation.model';
import { MessageResponse } from '../../../shared/models/message.model';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-conversation',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './conversation.component.html',
})
export class ConversationComponent implements OnInit, OnDestroy {
  private authService = inject(AuthService);
  private chatService = inject(ChatService);
  private wsService = inject(WebSocketService);

  conversation = signal<ConversationResponse | null>(null);
  messages = signal<MessageResponse[]>([]);
  messageContent = signal('');
  isLoading = signal(false);
  private subscription: Subscription | null = null;

  ngOnInit() {
    this.wsService.connect();
    this.subscription = this.wsService.messages$.subscribe((msg) => {
      this.messages.update((msgs) => [...msgs, msg]);
    });
  }

  openConversation() {
    this.isLoading.set(true);
    this.chatService.createConversation().subscribe({
      next: (conv) => {
        this.conversation.set(conv);
        this.wsService.subscribeToConversation(conv.id);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false),
    });
  }

  sendMessage() {
    const content = this.messageContent().trim();
    if (!content || !this.conversation()) return;

    this.wsService.sendMessage(this.conversation()!.id, content);
    this.messageContent.set('');
  }

  logout() {
    this.wsService.disconnect();
    this.authService.logout();
    window.location.href = '/login';
  }

  ngOnDestroy() {
    this.subscription?.unsubscribe();
    this.wsService.disconnect();
  }
}
