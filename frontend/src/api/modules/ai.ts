import { webHttp, unwrap } from "@/api/http";
import type { ApiResponse } from "@/types/api";
import type { AIChatResponse } from "@/types/ai";

export interface ChatMessage {
  role: "user" | "assistant";
  content: string;
}

export const sendChatMessage = async (messages: ChatMessage[]): Promise<AIChatResponse> => {
  const { data } = await webHttp.post<ApiResponse<AIChatResponse>>("/ai/chat", {
    messages
  });
  return unwrap(data);
};
