import { webHttp, unwrap } from "@/api/http";
import type { ApiResponse } from "@/types/api";

export interface FileUploadResult {
  url: string;
  file_name: string;
  content_type: string;
  size: number;
}

const MAX_IMAGE_SIZE = 5 * 1024 * 1024;
const ALLOWED_IMAGE_TYPES = ["image/jpeg", "image/png", "image/webp", "image/gif"];

export const uploadImage = async (file: File) => {
  if (file.size > MAX_IMAGE_SIZE) {
    throw new Error("图片大小不能超过 5MB");
  }
  if (!ALLOWED_IMAGE_TYPES.includes(file.type)) {
    throw new Error("仅支持 jpg、png、webp、gif 图片");
  }
  const formData = new FormData();
  formData.append("file", file);
  const { data } = await webHttp.post<ApiResponse<FileUploadResult>>("/files/upload", formData, {
    timeout: 30000
  });
  return unwrap(data);
};
