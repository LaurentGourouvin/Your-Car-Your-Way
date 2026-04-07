export interface ConversationResponse {
  id: string;
  userId: string;
  userFirstName: string;
  userLastName: string;
  agentId: string | null;
  status: 'WAITING' | 'IN_PROGRESS' | 'CLOSED';
  createdAt: string;
}
