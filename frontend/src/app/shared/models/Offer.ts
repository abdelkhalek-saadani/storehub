export interface Offer {
  id: string;
  label: string;
  imageUrl: string;
  alt: string;
  routerLink: string;
  featured?: boolean; // drives the large cell in masonry
}
