export interface Room {
  id: string;
  name: string;
  type: string;
  price: number;
  capacity: number;
  amenities: string[];
  available: boolean;
  images: number;
  size: string;
}

export interface Property {
  id: string;
  name: string;
  location: string;
  description: string;
  rating: number;
  reviews: number;
  rooms: Room[];
  featured: boolean;
  tags: string[];
}

export interface Booking {
  propertyId: string;
  propertyName: string;
  roomId: string;
  roomName: string;
  checkIn: string;
  checkOut: string;
  guests: number;
  total: number;
  status: 'confirmed' | 'pending' | 'cancelled';
}
