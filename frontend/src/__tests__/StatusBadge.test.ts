import { render } from "@testing-library/vue";
import StatusBadge from "../components/StatusBadge.vue";

describe("StatusBadge 组件", () => {
  it("显示 success 样式", () => {
    const { getByText } = render(StatusBadge, {
      slots: { default: "已完成" },
      props: { variant: "success" }
    });
    const badge = getByText("已完成");
    expect(badge).toBeTruthy();
    expect(badge.className).toContain("is-success");
  });

  it("显示 danger 样式", () => {
    const { getByText } = render(StatusBadge, {
      slots: { default: "失败" },
      props: { variant: "danger" }
    });
    const badge = getByText("失败");
    expect(badge).toBeTruthy();
    expect(badge.className).toContain("is-danger");
  });

  it("默认 neutral 样式", () => {
    const { getByText } = render(StatusBadge, {
      slots: { default: "默认状态" }
    });
    const badge = getByText("默认状态");
    expect(badge).toBeTruthy();
    expect(badge.className).toContain("is-neutral");
  });
});