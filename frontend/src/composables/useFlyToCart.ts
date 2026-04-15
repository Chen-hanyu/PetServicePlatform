/** 缩略图飞向页面内固定目标（如购物车 / 预约单图标） */
export function flyImageToTarget(
  imageUrl: string,
  fromEl: HTMLElement | null,
  targetId: string,
  bumpClass: string
) {
  if (typeof document === "undefined" || !imageUrl) return;
  
  // Give Vue time to update the badge count first
  setTimeout(() => {
    const target = document.getElementById(targetId);
    if (!target) return;

    // Calculate start and end positions
    let startLeft: number, startTop: number;
    const size = 52;

    if (fromEl) {
      const from = fromEl.getBoundingClientRect();
      startLeft = from.left + from.width / 2 - size / 2;
      startTop = from.top + from.height / 2 - size / 2;
    } else {
      // If no source element, start from center of screen
      startLeft = window.innerWidth / 2 - size / 2;
      startTop = window.innerHeight / 2 - size / 2;
    }

    const to = target.getBoundingClientRect();
    const endLeft = to.left + to.width / 2 - size / 2;
    const endTop = to.top + to.height / 2 - size / 2;

    target.classList.add(bumpClass);

    const wrap = document.createElement("div");
    wrap.setAttribute("data-fly-to-cart", "1");
    wrap.style.cssText = [
      "position:fixed",
      `left:${startLeft}px`,
      `top:${startTop}px`,
      `width:${size}px`,
      `height:${size}px`,
      "z-index:9999",
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
      wrap.style.transition = "transform 0.6s cubic-bezier(0.22, 0.68, 0.25, 1), opacity 0.5s ease";
      wrap.style.transform = `translate(${endLeft - startLeft}px, ${endTop - startTop}px) scale(0.1)`;
      wrap.style.opacity = "0";
    });

    window.setTimeout(() => {
      target.classList.remove(bumpClass);
      if (wrap.parentNode) wrap.remove();
    }, 700);
  }, 50);
}

/** 商品缩略图飞向右侧购物车图标（id=cart-target） */
export function flyImageToCart(imageUrl: string, fromEl: HTMLElement | null) {
  flyImageToTarget(imageUrl, fromEl, "cart-target", "bump");
}
