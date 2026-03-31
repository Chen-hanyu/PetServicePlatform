export interface HomeBanner {
  id: number;
  title: string;
  image_url: string;
  link_url?: string;
}

export interface HomeQuickEntry {
  code: string;
  title: string;
  path: string;
}

export interface HomeTip {
  title: string;
  content: string;
}

export interface PetCard {
  title: string;
  subtitle: string;
  image_url: string;
}

export interface HomeData {
  banners: HomeBanner[];
  quick_entries: HomeQuickEntry[];
  recommended_posts: Array<Record<string, unknown>>;
  recommended_services: Array<Record<string, unknown>>;
  recommended_products: Array<Record<string, unknown>>;
  tips: HomeTip[];
  pet_cards: PetCard[];
}
