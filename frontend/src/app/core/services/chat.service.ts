import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ConversationResponse } from '../../shared/models/conversation.model';
import { MessageResponse } from '../../shared/models/message.model';

@Injectable({ providedIn: 'root' })
export class ChatService {
  private http = inject(HttpClient);
  private readonly API_URL = 'http://localhost:8080/api/conversations';

  createConversation() {
    return this.http.post<ConversationResponse>(this.API_URL, {});
  }

  getQueue() {
    return this.http.get<ConversationResponse[]>(`${this.API_URL}/queue`);
  }

  takeConversation(id: string) {
    return this.http.patch<ConversationResponse>(`${this.API_URL}/${id}/take`, {});
  }

  closeConversation(id: string) {
    return this.http.patch<ConversationResponse>(`${this.API_URL}/${id}/close`, {});
  }

  getMessages(id: string) {
    return this.http.get<MessageResponse[]>(`${this.API_URL}/${id}/messages`);
  }

  getActiveConversation() {
    return this.http.get<ConversationResponse>(`${this.API_URL}/active`);
  }
}
