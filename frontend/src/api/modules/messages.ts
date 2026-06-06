import { webHttp, unwrap } from "@/api/http";
import type { ApiResponse, PageResult } from "@/types/api";

/** 消息类型 */
export interface MessageItem {
  id: number;
  type: string;
  title: string;
  content: string;
  time: string;
  isRead: boolean;
  link?: string;
  created_at?: string;
}

/** 获取消息列表 */
export const fetchMessages = async (params: Record<string, string | number | undefined>) => {
  const { data } = await webHttp.get<ApiResponse<PageResult<MessageItem>>>("/messages", { params });
  return unwrap(data);
};

/** 标记消息为已读 */
export const markMessageRead = async (messageId: number) => {
  const { data } = await webHttp.post<ApiResponse<void>>(`/messages/${messageId}/read`);
  return unwrap(data);
};

/** 全部标记已读 */
export const markAllMessagesRead = async () => {
  const { data } = await webHttp.post<ApiResponse<void>>("/messages/read-all");
  return unwrap(data);
};

/** 删除消息 */
export const deleteMessage = async (messageId: number) => {
  const { data } = await webHttp.delete<ApiResponse<void>>(`/messages/${messageId}`);
  return unwrap(data);
};
