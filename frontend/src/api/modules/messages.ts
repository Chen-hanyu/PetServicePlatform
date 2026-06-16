import { webHttp, unwrap } from "@/api/http";
import type { ApiResponse, PageResult } from "@/types/api";

type EntityId = string | number;

/** 消息类型 */
export interface MessageItem {
  id: EntityId;
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
export const markMessageRead = async (messageId: EntityId) => {
  const { data } = await webHttp.post<ApiResponse<void>>(`/messages/${messageId}/read`);
  return unwrap(data);
};

/** 全部标记已读 */
export const markAllMessagesRead = async () => {
  const { data } = await webHttp.post<ApiResponse<void>>("/messages/read-all");
  return unwrap(data);
};

/** 删除消息 */
export const deleteMessage = async (messageId: EntityId) => {
  const { data } = await webHttp.delete<ApiResponse<void>>(`/messages/${messageId}`);
  return unwrap(data);
};

/** 提交在线客服咨询 */
export const submitSupportMessage = async (payload: { content: string; source?: string }) => {
  const { data } = await webHttp.post<ApiResponse<MessageItem>>("/messages/support", payload);
  return unwrap(data);
};
