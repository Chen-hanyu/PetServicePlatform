import { render } from "@testing-library/vue";
import DataState from "../components/DataState.vue";

describe("DataState 组件", () => {
  it("loading 显示加载中", () => {
    const { getByText } = render(DataState, { props: { loading: true } });
    expect(getByText("加载中...")).toBeTruthy();
  });

  it("error 显示错误信息", () => {
    const { getByText } = render(DataState, { props: { error: "出错了" } });
    expect(getByText("出错了")).toBeTruthy();
  });

  it("empty 显示暂无数据", () => {
    const { getByText } = render(DataState, { props: { empty: true } });
    expect(getByText("暂无数据")).toBeTruthy();
  });

  it("empty 配合自定义 emptyText", () => {
    const { getByText } = render(DataState, {
      props: { empty: true, emptyText: "没有内容" }
    });
    expect(getByText("没有内容")).toBeTruthy();
  });

  it("正常状态下显示 slot 内容", () => {
    const { getByText } = render(DataState, {
      slots: { default: "实际数据" }
    });
    expect(getByText("实际数据")).toBeTruthy();
  });
});