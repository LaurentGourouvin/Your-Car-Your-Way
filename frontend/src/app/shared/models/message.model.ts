export interface MessageResponse {
  id: string;
  senderId: string;
  senderType: 'CLIENT' | 'SUPPORT';
  content: string;
  sentAt: string;
}

export interface SendMessageRequest {
  content: string;
}
