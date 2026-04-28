import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, waitFor } from "@testing-library/vue";
import userEvent from "@testing-library/user-event";
import AIPetDoctorChat from "../components/ai/AIPetDoctorChat.vue";

vi.mock("@/api/modules/ai", () => ({
  sendChatMessage: vi.fn()
}));

import { sendChatMessage } from "@/api/modules/ai";

describe("AIPetDoctorChat - 模拟 API 测试", () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  it("发送消息成功，显示回复内容", async () => {
    (sendChatMessage as any).mockResolvedValue({ reply: "猫咪可能是感冒了" });

    render(AIPetDoctorChat, { props: { open: true } });

    const textarea = screen.getByPlaceholderText("输入你的问题...");
    const sendBtn = screen.getByRole("button", { name: "" });

    await userEvent.type(textarea, "猫咪打喷嚏");
    await userEvent.click(sendBtn);

    await waitFor(() => {
      expect(screen.getByText("猫咪可能是感冒了")).toBeTruthy();
    });
    expect(sendChatMessage).toHaveBeenCalledTimes(1);
  });

  it("发送消息失败，显示错误信息", async () => {
    (sendChatMessage as any).mockRejectedValue(new Error("网络错误"));

    render(AIPetDoctorChat, { props: { open: true } });
    const textarea = screen.getByPlaceholderText("输入你的问题...");
    const sendBtn = screen.getByRole("button", { name: "" });

    await userEvent.type(textarea, "狗狗拉肚子");
    await userEvent.click(sendBtn);

    await waitFor(() => {
      expect(screen.getByText(/抱歉，遇到了一个问题：网络错误/)).toBeTruthy();
    });
  });

  it("点击快捷问题按钮，自动发送并回复", async () => {
    (sendChatMessage as any).mockResolvedValue({ reply: "请立即带宠物就医" });

    render(AIPetDoctorChat, { props: { open: true } });
    const quickBtn = screen.getByRole("button", { name: "狗狗可以吃巧克力吗？" });
    await userEvent.click(quickBtn);

    await waitFor(() => {
      expect(screen.getByText("请立即带宠物就医")).toBeTruthy();
    });
    expect(sendChatMessage).toHaveBeenCalled();
  });

  it("输入为空时，发送按钮禁用，不调用 API", async () => {
    render(AIPetDoctorChat, { props: { open: true } });
    const sendBtn = screen.getByRole("button", { name: "" });
    // 直接检查 disabled 属性
    expect(sendBtn.disabled).toBe(true);

    await userEvent.click(sendBtn);
    expect(sendChatMessage).not.toHaveBeenCalled();
  });
});