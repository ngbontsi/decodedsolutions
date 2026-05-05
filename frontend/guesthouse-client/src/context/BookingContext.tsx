import { createContext, useContext, useState, useCallback, type ReactNode } from 'react';
import { properties } from '../data/properties';
import type { Property, Booking } from '../types';

interface BookingContextType {
  properties: Property[];
  bookings: Booking[];
  addBooking: (booking: Booking) => void;
  cancelBooking: (bookingId: string) => void;
}

const BookingContext = createContext<BookingContextType | null>(null);

export function BookingProvider({ children }: { children: ReactNode }) {
  const [bookings, setBookings] = useState<Booking[]>([
    {
      propertyId: '1',
      propertyName: 'Ocean View Lodge',
      roomId: '101',
      roomName: 'Ocean Suite',
      checkIn: '2024-06-15',
      checkOut: '2024-06-18',
      guests: 2,
      total: 7500,
      status: 'confirmed',
    },
  ]);

  const addBooking = useCallback((booking: Booking) => {
    setBookings((prev) => [...prev, booking]);
  }, []);

  const cancelBooking = useCallback((bookingId: string) => {
    setBookings((prev) =>
      prev.map((b) =>
        b.propertyId === bookingId ? { ...b, status: 'cancelled' as const } : b
      )
    );
  }, []);

  return (
    <BookingContext.Provider value={{ properties, bookings, addBooking, cancelBooking }}>
      {children}
    </BookingContext.Provider>
  );
}

export function useBooking() {
  const ctx = useContext(BookingContext);
  if (!ctx) throw new Error('useBooking must be used within BookingProvider');
  return ctx;
}
