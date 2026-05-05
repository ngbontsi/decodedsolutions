export interface Product {
  id: number;
  name: string;
  category: 'beef' | 'lamb' | 'chicken' | 'pork' | 'specials';
  price: number;
  unit: string;
  image: string;
  description: string;
  inStock: boolean;
  featured: boolean;
}

export interface CartItem {
  product: Product;
  quantity: number;
}

export type Category = Product['category'];
