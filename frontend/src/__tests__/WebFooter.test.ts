import { render } from "@testing-library/vue";
import WebFooter from "../components/WebFooter.vue";

describe("WebFooter 组件", () => {
  it("显示版权信息", () => {
    const { getByText } = render(WebFooter);
    expect(getByText(/宠物之家 PetHome/)).toBeTruthy();
  });
});