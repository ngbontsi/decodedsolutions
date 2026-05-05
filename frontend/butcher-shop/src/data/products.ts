import type { Product } from '../types';

export const products: Product[] = [
  { id: 1, name: 'Beef Fillet Steak', category: 'beef', price: 189.99, unit: 'kg', image: '🥩', description: 'Premium grass-fed fillet, perfect for grilling', inStock: true, featured: true },
  { id: 2, name: 'Beef Rump Steak', category: 'beef', price: 139.99, unit: 'kg', image: '🥩', description: 'Tender rump cut with rich flavour', inStock: true, featured: false },
  { id: 3, name: 'Beef Mince', category: 'beef', price: 79.99, unit: 'kg', image: '🥩', description: 'Lean beef mince, ideal for everyday cooking', inStock: true, featured: false },
  { id: 4, name: 'Beef Short Ribs', category: 'beef', price: 159.99, unit: 'kg', image: '🥩', description: 'Slow-cook short ribs, fall-off-the-bone tender', inStock: true, featured: false },
  { id: 5, name: 'Lamb Leg Roast', category: 'lamb', price: 149.99, unit: 'kg', image: '🍖', description: 'Whole lamb leg, perfect for Sunday roast', inStock: true, featured: true },
  { id: 6, name: 'Lamb Chops', category: 'lamb', price: 179.99, unit: 'kg', image: '🍖', description: 'Premium loin chops, quick and easy to cook', inStock: true, featured: false },
  { id: 7, name: 'Lamb Shoulder', category: 'lamb', price: 119.99, unit: 'kg', image: '🍖', description: 'Slow-roast shoulder with incredible flavour', inStock: false, featured: false },
  { id: 8, name: 'Whole Chicken', category: 'chicken', price: 69.99, unit: 'each', image: '🍗', description: 'Free-range whole chicken, approx 1.8kg', inStock: true, featured: true },
  { id: 9, name: 'Chicken Breast Fillets', category: 'chicken', price: 89.99, unit: 'kg', image: '🍗', description: 'Boneless skinless breast fillets', inStock: true, featured: false },
  { id: 10, name: 'Chicken Wings', category: 'chicken', price: 59.99, unit: 'kg', image: '🍗', description: 'Wings perfect for braai or oven baking', inStock: true, featured: false },
  { id: 11, name: 'Pork Belly', category: 'pork', price: 99.99, unit: 'kg', image: '🥓', description: 'Fatty pork belly for crispy crackling', inStock: true, featured: false },
  { id: 12, name: 'Pork Chops', category: 'pork', price: 109.99, unit: 'kg', image: '🥓', description: 'Thick-cut pork chops, juicy and tender', inStock: true, featured: false },
  { id: 13, name: 'Boerewors (5kg)', category: 'specials', price: 349.99, unit: 'pack', image: '⭐', description: 'Traditional boerewors roll, fresh this week', inStock: true, featured: true },
  { id: 14, name: 'Mixed Braai Pack', category: 'specials', price: 499.99, unit: 'pack', image: '⭐', description: 'Steaks, chops, wors & sosaties for the whole family', inStock: true, featured: true },
  { id: 15, name: 'Biltong (Dry)', category: 'specials', price: 399.99, unit: 'kg', image: '⭐', description: 'Premium dry biltong, made in-house', inStock: true, featured: false },
];
