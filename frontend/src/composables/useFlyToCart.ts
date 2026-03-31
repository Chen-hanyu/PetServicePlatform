/** 商品缩略图飞向右侧购物车图标的动效（目标节点 id=commerce-cart-target） */
export function flyImageToCart(imageUrl: string, fromEl: HTMLElement | null) {
  if (!fromEl || typeof document === "undefined") return;
  const target = document.getElementById("commerce-cart-target");
  if (!target) return;

  const from = fromEl.getBoundingClientRect();
  const to = target.getBoundingClientRect();

  const size = 52;
  const startLeft = from.left + from.width / 2 - size / 2;
  const startTop = from.top + from.height / 2 - size / 2;
  const endLeft = to.left + to.width / 2 - size / 2;
  const endTop = to.top + to.height / 2 - size / 2;

  target.classList.add("commerce-cart-bump");

  const wrap = document.createElement("div");
  wrap.setAttribute("data-fly-to-cart", "1");
  wrap.style.cssText = [
    "position:fixed",
    `left:${startLeft}px`,
    `top:${startTop}px`,
    `width:${size}px`,
    `height:${size}px`,
    "z-index:10000",
    "pointer-events:none",
    "border-radius:10px",
    "overflow:hidden",
    "box-shadow:0 6px 22px rgba(61,47,40,.28)",
    "will-change:transform,opacity"
  ].join(";");

  const img = document.createElement("img");
  img.src = imageUrl;
  img.alt = "";
  img.style.cssText = "width:100%;height:100%;object-fit:cover;display:block";
  wrap.appendChild(img);
  document.body.appendChild(wrap);

  requestAnimationFrame(() => {
    wrap.style.transition = "transform 0.62s cubic-bezier(0.22, 0.68, 0.25, 1), opacity 0.62s ease";
    wrap.style.transform = `translate(${endLeft - startLeft}px, ${endTop - startTop}px) scale(0.18)`;
    wrap.style.opacity = "0.15";
  });

  window.setTimeout(() => {
    target.classList.remove("commerce-cart-bump");
    wrap.remove();
  }, 650);
}
