import { webHttp, unwrap } from "@/api/http";
import type { ApiResponse } from "@/types/api";

export interface FileUploadResult {
  url: string;
  file_name: string;
  content_type: string;
  size: number;
}

export const uploadImage = async (file: File) => {
  const formData = new FormData();
  formData.append("file", file);
  const { data } = await webHttp.post<ApiResponse<FileUploadResult>>("/files/upload", formData, {
    headers: { "Content-Type": "multipart/form-data" },
    timeout: 30000
  });
  return unwrap(data);
};
