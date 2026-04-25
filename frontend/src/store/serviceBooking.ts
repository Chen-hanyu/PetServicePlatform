import { defineStore } from "pinia";

export interface ServiceBookingAddon {
  id: string;
  name: string;
  price: number;
  group?: string;
}

export interface ServiceBookingMain {
  id: number;
  name: string;
  price: number;
  duration?: string;
}

function activePackageForState(
  merchantId: number | null,
  mainIds: number[],
  selectedPackageId: string | null
) {
  void merchantId;
  void mainIds;
  void selectedPackageId;
  return null;
}

export const useServiceBookingStore = defineStore("serviceBooking", {
  state: () => ({
    merchantId: null as number | null,
    merchantName: "",
    merchantCover: "",
    mainServices: [] as ServiceBookingMain[],
    selectedPackageId: null as string | null,
    addons: [] as ServiceBookingAddon[],
    petLabel: "小小黑 (金毛)",
    bookingDate: "",
    timeSlot: "",
    remark: "",
    contactName: "",
    contactPhone: ""
  }),
  getters: {
    mainServicesSubtotal(state): number {
      return state.mainServices.reduce((s, m) => s + m.price, 0);
    },
    appliedPackage() {
      return activePackageForState(
        this.merchantId,
        this.mainServices.map((m) => m.id),
        this.selectedPackageId
      );
    },
    /** 主服务应付金额（含套餐价） */
    mainTotal(): number {
      const pkg = this.appliedPackage;
      if (pkg) return pkg.price;
      return this.mainServicesSubtotal;
    },
    packageSavings(): number {
      const pkg = this.appliedPackage;
      if (!pkg) return 0;
      return Math.max(0, this.mainServicesSubtotal - pkg.price);
    },
    addonsTotal(state): number {
      return state.addons.reduce((s, a) => s + a.price, 0);
    },
    totalAmount(): number {
      return this.mainTotal + this.addonsTotal;
    },
    hasDraft(state): boolean {
      return state.merchantId != null && state.mainServices.length > 0;
    },
    scheduleReady(state): boolean {
      return Boolean(state.bookingDate && state.timeSlot);
    },
    badgeCount(state): number {
      return state.mainServices.length + state.addons.length;
    }
  },
  actions: {
    setMerchant(m: { id: number; name: string; cover_url?: string }) {
      if (this.merchantId != null && this.merchantId !== m.id) {
        this.mainServices = [];
        this.selectedPackageId = null;
        this.addons = [];
        this.bookingDate = "";
        this.timeSlot = "";
      }
      this.merchantId = m.id;
      this.merchantName = m.name;
      this.merchantCover = m.cover_url || "";
    },
    toggleMainService(s: ServiceBookingMain) {
      const beforeKey = this.mainServices
        .map((m) => m.id)
        .sort((a, b) => a - b)
        .join(",");
      const i = this.mainServices.findIndex((x) => x.id === s.id);
      if (i >= 0) this.mainServices.splice(i, 1);
      else this.mainServices.push(s);
      const afterKey = this.mainServices
        .map((m) => m.id)
        .sort((a, b) => a - b)
        .join(",");
      if (beforeKey !== afterKey) this.addons = [];
      this.syncPackageSelection();
    },
    setMainServices(list: ServiceBookingMain[]) {
      this.mainServices = [...list];
      this.syncPackageSelection();
      this.addons = [];
    },
    selectPackage(serviceIds: number[], packageId: string, catalog: ServiceBookingMain[]) {
      const picked = serviceIds
        .map((id) => catalog.find((c) => c.id === id))
        .filter((x): x is ServiceBookingMain => Boolean(x));
      if (picked.length !== serviceIds.length) return;
      this.mainServices = picked;
      this.selectedPackageId = packageId;
      this.addons = [];
    },
    clearPackage() {
      this.selectedPackageId = null;
    },
    syncPackageSelection() {
      if (!this.selectedPackageId) return;
      const ok = activePackageForState(
        this.merchantId,
        this.mainServices.map((m) => m.id),
        this.selectedPackageId
      );
      if (!ok) this.selectedPackageId = null;
    },
    clearAddons() {
      this.addons = [];
    },
    toggleAddon(a: ServiceBookingAddon) {
      const i = this.addons.findIndex((x) => x.id === a.id);
      if (i >= 0) this.addons.splice(i, 1);
      else this.addons.push(a);
    },
    isAddonOn(id: string) {
      return this.addons.some((x) => x.id === id);
    },
    isMainOn(id: number) {
      return this.mainServices.some((x) => x.id === id);
    },
    setSchedule(partial: Partial<{ bookingDate: string; timeSlot: string; petLabel: string; remark: string }>) {
      if (partial.bookingDate !== undefined) this.bookingDate = partial.bookingDate;
      if (partial.timeSlot !== undefined) this.timeSlot = partial.timeSlot;
      if (partial.petLabel !== undefined) this.petLabel = partial.petLabel;
      if (partial.remark !== undefined) this.remark = partial.remark;
    },
    setContact(name: string, phone: string) {
      this.contactName = name;
      this.contactPhone = phone;
    },
    clear() {
      this.merchantId = null;
      this.merchantName = "";
      this.merchantCover = "";
      this.mainServices = [];
      this.selectedPackageId = null;
      this.addons = [];
      this.petLabel = "小小黑 (金毛)";
      this.bookingDate = "";
      this.timeSlot = "";
      this.remark = "";
      this.contactName = "";
      this.contactPhone = "";
    }
  }
});
