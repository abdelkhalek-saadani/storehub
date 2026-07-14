export interface ProductFilters {
  categories: string[];
  minPrice: number | null;
  maxPrice: number | null;
  saleEvent: string | null;
  isBestSeller: boolean | null;
}
