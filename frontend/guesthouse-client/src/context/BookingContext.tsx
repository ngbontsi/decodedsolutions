import { createContext, useContext, useState, useEffect, useCallback, type ReactNode } from 'react';
import api from '../services/api';
import mockProperties from '../data/mockProperties';
import type { Property, Room, Booking } from '../types';

interface AuthUser {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
}

interface BookingContextType {
  user: AuthUser | null;
  token: string | null;
  isAuthenticated: boolean;
  properties: Property[];
  bookings: Booking[];
  loading: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (data: { email: string; password: string; firstName: string; lastName: string }) => Promise<void>;
  logout: () => void;
  addBooking: (booking: Omit<Booking, 'id' | 'status'>) => Promise<void>;
  cancelBooking: (bookingId: string) => Promise<void>;
}

const BookingContext = createContext<BookingContextType | null>(null);

function mapRoom(r: any): Room {
  return {
    id: r.id,
    name: r.roomNumber || 'Room',
    type: r.type || 'Standard',
    price: Number(r.pricePerNight),
    capacity: r.capacity || 2,
    amenities: (r.amenities || '').split(',').map((s: string) => s.trim()).filter(Boolean),
    available: r.available !== false,
    images: 3,
    size: '30m²',
  };
}

function mapGuesthouseToProperty(g: any, rooms: any[]): Property {
  return {
    id: g.id,
    name: g.name,
    location: g.address || 'Unknown',
    description: g.description || '',
    rating: 4.0,
    reviews: 0,
    rooms: (rooms || []).map(mapRoom),
    featured: false,
    tags: (g.amenities || '').split(',').map((s: string) => s.trim()).filter(Boolean),
  };
}

function mapReservationToBooking(r: any): Booking {
  return {
    id: r.id,
    propertyId: r.roomId || '',
    propertyName: `Room ${r.roomId || ''}`,
    roomId: r.roomId,
    roomName: `Room ${r.roomId || ''}`,
    checkIn: r.checkInDate || '',
    checkOut: r.checkOutDate || '',
    guests: r.numberOfGuests || 1,
    total: r.totalPrice || 0,
    status: (r.status || 'pending').toLowerCase() as Booking['status'],
  };
}

export function BookingProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [properties, setProperties] = useState<Property[]>([]);
  const [bookings, setBookings] = useState<Booking[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const savedToken = localStorage.getItem('gh_token');
    const savedUser = localStorage.getItem('gh_user');
    if (savedToken && savedUser) {
      setToken(savedToken);
      setUser(JSON.parse(savedUser));
    }
  }, []);

  const fetchProperties = useCallback(async () => {
    try {
      const res = await api.get('/api/guesthouse/guesthouses/active');
      const ghList = res.data;
      const props: Property[] = [];
      for (const g of ghList) {
        let rooms: any[] = [];
        try {
          const rr = await api.get(`/api/guesthouse/rooms/guesthouse/${g.id}`);
          rooms = rr.data || [];
        } catch { /* no rooms */ }
        props.push(mapGuesthouseToProperty(g, rooms));
      }
      setProperties(props);
    } catch {
      setProperties(mockProperties);
    } finally {
      setLoading(false);
    }
  }, []);

  const fetchBookings = useCallback(async () => {
    if (!token) { setBookings([]); return; }
    try {
      const res = await api.get('/api/guesthouse/reservations/my');
      setBookings((res.data || []).map(mapReservationToBooking));
    } catch {
      setBookings([]);
    }
  }, [token]);

  useEffect(() => { fetchProperties(); }, [fetchProperties]);

  useEffect(() => { fetchBookings(); }, [fetchBookings]);

  const login = async (email: string, password: string) => {
    const res = await api.post('/api/auth/login', { email, password });
    const d = res.data;
    const u: AuthUser = { id: d.userId, email: d.email, firstName: d.firstName, lastName: d.lastName };
    localStorage.setItem('gh_token', d.token);
    localStorage.setItem('gh_user', JSON.stringify(u));
    setToken(d.token);
    setUser(u);
    await fetchBookings();
  };

  const register = async (data: { email: string; password: string; firstName: string; lastName: string }) => {
    const res = await api.post('/api/auth/register', data);
    const d = res.data;
    const u: AuthUser = { id: d.userId, email: d.email, firstName: d.firstName, lastName: d.lastName };
    localStorage.setItem('gh_token', d.token);
    localStorage.setItem('gh_user', JSON.stringify(u));
    setToken(d.token);
    setUser(u);
    await fetchBookings();
  };

  const logout = () => {
    const t = localStorage.getItem('gh_token');
    if (t) api.post('/api/auth/logout', null, { headers: { Authorization: `Bearer ${t}` } }).catch(() => {});
    localStorage.removeItem('gh_token');
    localStorage.removeItem('gh_user');
    setToken(null);
    setUser(null);
    setBookings([]);
  };

  const addBooking = async (booking: Omit<Booking, 'id' | 'status'>) => {
    try {
      await api.post('/api/guesthouse/reservations', {
        roomId: booking.roomId,
        checkInDate: booking.checkIn,
        checkOutDate: booking.checkOut,
        numberOfGuests: booking.guests,
      });
      await fetchBookings();
    } catch { /* ignore */ }
  };

  const cancelBooking = async (bookingId: string) => {
    try {
      await api.patch(`/api/guesthouse/reservations/${bookingId}/cancel`);
      await fetchBookings();
    } catch { /* ignore */ }
  };

  return (
    <BookingContext.Provider value={{
      user, token, isAuthenticated: !!token, properties, bookings, loading,
      login, register, logout, addBooking, cancelBooking,
    }}>
      {children}
    </BookingContext.Provider>
  );
}

export function useBooking() {
  const ctx = useContext(BookingContext);
  if (!ctx) throw new Error('useBooking must be used within BookingProvider');
  return ctx;
}
