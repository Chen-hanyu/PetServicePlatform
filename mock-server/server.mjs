import http from "node:http";
import { URL } from "node:url";

const PORT = Number(process.env.MOCK_API_PORT || 8080);

const cors = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers": "*",
  "Access-Control-Allow-Methods": "GET,POST,PUT,DELETE,PATCH,OPTIONS"
};

function sendJson(res, status, body) {
  res.writeHead(status, { "Content-Type": "application/json; charset=utf-8", ...cors });
  res.end(JSON.stringify(body));
}

function ok(res, data) {
  sendJson(res, 200, { code: 0, message: "ok", data });
}

function pageList(list, query) {
  const page = Number(query.get("page") || 1);
  const pageSize = Number(query.get("page_size") || 20);
  return { list, total: list.length, page, page_size: pageSize };
}

function readBody(req) {
  return new Promise((resolve) => {
    let raw = "";
    req.on("data", (c) => {
      raw += c;
    });
    req.on("end", () => {
      if (!raw) {
        resolve({});
        return;
      }
      try {
        resolve(JSON.parse(raw));
      } catch {
        resolve({});
      }
    });
  });
}

const homeData = {
  banners: [
    {
      id: 1,
      title: "萌宠服务节（Mock）",
      image_url: "https://picsum.photos/seed/petbanner/800/360",
      link_url: "/shop"
    }
  ],
  quick_entries: [
    { code: "adoption", title: "领养", path: "/adoption" },
    { code: "services", title: "服务", path: "/services" },
    { code: "shop", title: "商城", path: "/shop" },
    { code: "community", title: "社区", path: "/community" }
  ],
  recommended_posts: [],
  recommended_services: [],
  recommended_products: [],
  tips: [{ title: "提示", content: "当前为 mock-server 返回的数据，用于前端联调与截图。" }],
  pet_cards: [
    {
      title: "Mock 数据",
      subtitle: "启动 mock-server 后即可看到接口响应",
      image_url: "https://picsum.photos/seed/petcard/400/300"
    }
  ]
};

const postList = [
  {
    id: 1,
    title: "Mock：新手养猫小贴士",
    category: "recommended",
    cover_url: "https://picsum.photos/seed/postcover/640/360",
    excerpt: "这是社区列表的 mock 数据，Network 里应能看到 JSON。",
    status: "published",
    like_count: 12,
    favorite_count: 3,
    comment_count: 5,
    author: { id: 1, nickname: "MockUser", avatar_url: "https://i.pravatar.cc/150?u=1" },
    tags: ["mock"],
    published_at: new Date().toISOString()
  }
];

const adoptionPets = [
  {
    id: 101,
    name: "Mock 小猫",
    type: "cat",
    breed: "英国短毛",
    gender: "female",
    age_desc: "6 个月",
    city: "上海",
    health_status: "已驱虫",
    status: "available",
    cover_url: "https://picsum.photos/seed/adoptpet/400/300"
  }
];

async function handle(req, res) {
  if (req.method === "OPTIONS") {
    res.writeHead(204, cors);
    res.end();
    return;
  }

  const host = req.headers.host || "127.0.0.1";
  const u = new URL(req.url || "/", `http://${host}`);
  const path = u.pathname;
  const query = u.searchParams;

  if (req.method === "GET" && path === "/api/v1/home") {
    ok(res, homeData);
    return;
  }

  if (req.method === "GET" && path === "/api/v1/community/posts") {
    ok(res, pageList(postList, query));
    return;
  }

  if (req.method === "GET" && /^\/api\/v1\/community\/posts\/\d+$/.test(path)) {
    const id = Number(path.split("/").pop());
    ok(res, {
      ...postList[0],
      id,
      content: "<p>Mock 帖子正文（HTML）。</p>",
      images: [],
      is_liked: false,
      is_favorited: false
    });
    return;
  }

  if (req.method === "GET" && /^\/api\/v1\/community\/posts\/\d+\/comments$/.test(path)) {
    ok(res, pageList([], query));
    return;
  }

  if (req.method === "GET" && path === "/api/v1/adoption/pets") {
    ok(res, pageList(adoptionPets, query));
    return;
  }

  if (req.method === "GET" && /^\/api\/v1\/adoption\/pets\/\d+$/.test(path)) {
    const id = Number(path.split("/").pop());
    ok(res, {
      ...adoptionPets[0],
      id,
      personality: "活泼",
      adoption_requirements: "有养宠经验",
      story: "Mock 领养故事"
    });
    return;
  }

  if (req.method === "GET" && path === "/api/v1/adoption/process") {
    ok(res, { title: "领养流程（Mock）", steps: ["提交申请", "家访沟通", "签署协议", "接宝贝回家"] });
    return;
  }

  if (req.method === "GET" && path === "/api/v1/pets") {
    ok(res, []);
    return;
  }

  if (req.method === "GET" && path === "/api/v1/profile/overview") {
    ok(res, {
      user: {
        id: 1,
        role: "USER",
        phone: "13800000000",
        nickname: "Mock 用户",
        avatar_url: "https://i.pravatar.cc/150?u=mock",
        gender: "unknown",
        bio: "",
        status: "active"
      },
      pet_count: 0,
      post_count: 0,
      favorite_count: 0,
      order_count: 0,
      booking_count: 0,
      adoption_application_count: 0,
      unread_message_count: 0
    });
    return;
  }

  if (req.method === "GET" && path === "/api/v1/services/categories") {
    ok(res, [{ id: 1, name: "洗护美容", sort: 1, status: "active" }]);
    return;
  }

  if (req.method === "GET" && path === "/api/v1/services/merchants") {
    const list = [
      {
        id: 1,
        name: "Mock 宠物店",
        district: "朝阳区",
        address: "Mock 路 1 号",
        score: 4.8,
        business_hours: "09:00-21:00",
        status: "open"
      }
    ];
    ok(res, pageList(list, query));
    return;
  }

  if (req.method === "GET" && /^\/api\/v1\/services\/merchants\/\d+$/.test(path)) {
    ok(res, {
      id: 1,
      name: "Mock 宠物店",
      district: "朝阳区",
      address: "Mock 路 1 号",
      score: 4.8,
      business_hours: "09:00-21:00",
      status: "open",
      phone: "400-0000-000",
      services: [{ id: 1, name: "洗澡", price: 88 }],
      reviews: []
    });
    return;
  }

  if (req.method === "GET" && path === "/api/v1/shop/categories") {
    ok(res, [{ id: 1, name: "猫粮", pet_type: "cat", sort: 1, status: "active" }]);
    return;
  }

  if (req.method === "GET" && path === "/api/v1/shop/products") {
    const list = [
      {
        id: 1,
        category_id: 1,
        name: "Mock 猫粮",
        subtitle: "幼猫专用（示例）",
        image_url: "https://picsum.photos/seed/product/400/400",
        price: 99,
        stock: 100,
        pet_type: "cat",
        status: "on_sale"
      }
    ];
    ok(res, pageList(list, query));
    return;
  }

  if (req.method === "GET" && /^\/api\/v1\/shop\/products\/\d+$/.test(path)) {
    const id = Number(path.split("/").pop());
    ok(res, {
      id,
      category_id: 1,
      name: "Mock 猫粮",
      subtitle: "幼猫专用（示例）",
      image_url: "https://picsum.photos/seed/product/400/400",
      price: 99,
      stock: 100,
      pet_type: "cat",
      status: "on_sale",
      images: ["https://picsum.photos/seed/product2/800/800"],
      description: "Mock 商品详情"
    });
    return;
  }

  if (req.method === "GET" && path === "/api/v1/shop/cart") {
    ok(res, { items: [], total_amount: 0 });
    return;
  }

  if (req.method === "POST" && path === "/api/v1/auth/login") {
    await readBody(req);
    ok(res, {
      token: "mock-token",
      token_type: "Bearer",
      expires_in: 86400,
      user: {
        id: 1,
        role: "USER",
        phone: "13800000000",
        nickname: "Mock 用户",
        avatar_url: "https://i.pravatar.cc/150?u=mock",
        gender: "unknown",
        bio: "",
        status: "active"
      }
    });
    return;
  }

  if (req.method === "POST" && path.startsWith("/api/v1/")) {
    await readBody(req);
    ok(res, { ok: true });
    return;
  }

  sendJson(res, 404, { code: 404, message: "not found", data: null });
}

const server = http.createServer((req, res) => {
  handle(req, res).catch(() => {
    sendJson(res, 500, { code: 500, message: "mock server error", data: null });
  });
});

server.listen(PORT, "127.0.0.1", () => {
  console.log(`Mock API: http://127.0.0.1:${PORT}  (paths under /api/v1)`);
});
