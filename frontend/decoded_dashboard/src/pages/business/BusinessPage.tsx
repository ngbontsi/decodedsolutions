import { useState } from "react";
import type {
  Restaurant,
  Guesthouse,
  MarketplaceOrder,
  Task,
} from "../../types";

const mockRestaurants: Restaurant[] = [
  {
    id: "1",
    name: "Zozo food shop",
    cuisine: "Cradock",
    rating: 4.5,
    active: true,
    menuItemsCount: 32,
  },
  {
    id: "2",
    name: "Yolisa food shop",
    cuisine: "Khayelitsha",
    rating: 4.8,
    active: true,
    menuItemsCount: 45,
  },
  {
    id: "3",
    name: "Kouksie bar",
    cuisine: "Cradock",
    rating: 4.2,
    active: false,
    menuItemsCount: 28,
  },
];

const mockGuesthouses: Guesthouse[] = [
  {
    id: "1",
    name: "Rasmen and Sons",
    location: "Nxuba Lingelihle",
    rating: 4.6,
    active: true,
    roomsCount: 24,
    reservationsCount: 18,
  },
  {
    id: "2",
    name: "Mbulelo Lourge",
    location: "Nxuba Mpolweni",
    rating: 4.9,
    active: true,
    roomsCount: 12,
    reservationsCount: 10,
  },
  {
    id: "3",
    name: "Pilitie",
    location: "Nxuba Michuasdal",
    rating: 3.8,
    active: true,
    roomsCount: 40,
    reservationsCount: 35,
  },
];

const mockOrders: MarketplaceOrder[] = [
  {
    id: "ORD-001",
    customerName: "Alice M.",
    total: 89.5,
    status: "completed",
    itemsCount: 3,
    createdAt: "2024-05-01",
  },
  {
    id: "ORD-002",
    customerName: "Bob K.",
    total: 45.0,
    status: "processing",
    itemsCount: 2,
    createdAt: "2024-05-02",
  },
  {
    id: "ORD-003",
    customerName: "Carol W.",
    total: 120.75,
    status: "pending",
    itemsCount: 5,
    createdAt: "2024-05-03",
  },
  {
    id: "ORD-004",
    customerName: "Dave R.",
    total: 33.25,
    status: "cancelled",
    itemsCount: 1,
    createdAt: "2024-05-03",
  },
];

const mockTasks: Task[] = [
  {
    id: "1",
    title: "Fix payment gateway timeout",
    status: "in_progress",
    priority: "high",
    createdAt: "2024-05-01",
  },
  {
    id: "2",
    title: "Add restaurant onboarding flow",
    status: "pending",
    priority: "medium",
    createdAt: "2024-05-02",
  },
  {
    id: "3",
    title: "Update guesthouse search filters",
    status: "completed",
    priority: "low",
    createdAt: "2024-05-03",
  },
];

type Tab = "restaurants" | "guesthouses" | "orders" | "tasks";

export default function BusinessPage() {
  const [tab, setTab] = useState<Tab>("restaurants");
  const tabs: { key: Tab; label: string }[] = [
    { key: "restaurants", label: "Restaurants" },
    { key: "guesthouses", label: "Guesthouses" },
    { key: "orders", label: "Orders" },
    { key: "tasks", label: "Tasks" },
  ];

  return (
    <div className="page">
      <div className="page-header">
        <h1>Business Data</h1>
        <p>Restaurants, guesthouses, orders, and tasks</p>
      </div>

      <div className="tabs">
        {tabs.map((t) => (
          <button
            key={t.key}
            className={`tab ${tab === t.key ? "active" : ""}`}
            onClick={() => setTab(t.key)}
          >
            {t.label}
          </button>
        ))}
      </div>

      <div className="card">
        <div className="table-wrapper">
          {tab === "restaurants" && (
            <table className="data-table">
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Cuisine</th>
                  <th>Rating</th>
                  <th>Menu Items</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {mockRestaurants.map((r) => (
                  <tr key={r.id}>
                    <td>{r.name}</td>
                    <td>{r.cuisine}</td>
                    <td>{r.rating}</td>
                    <td>{r.menuItemsCount}</td>
                    <td>
                      <span
                        className={`status-indicator ${r.active ? "enabled" : "disabled"}`}
                      >
                        {r.active ? "Active" : "Inactive"}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}

          {tab === "guesthouses" && (
            <table className="data-table">
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Location</th>
                  <th>Rating</th>
                  <th>Rooms</th>
                  <th>Reservations</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {mockGuesthouses.map((g) => (
                  <tr key={g.id}>
                    <td>{g.name}</td>
                    <td>{g.location}</td>
                    <td>{g.rating}</td>
                    <td>{g.roomsCount}</td>
                    <td>{g.reservationsCount}</td>
                    <td>
                      <span
                        className={`status-indicator ${g.active ? "enabled" : "disabled"}`}
                      >
                        {g.active ? "Active" : "Inactive"}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}

          {tab === "orders" && (
            <table className="data-table">
              <thead>
                <tr>
                  <th>Order ID</th>
                  <th>Customer</th>
                  <th>Items</th>
                  <th>Total</th>
                  <th>Status</th>
                  <th>Date</th>
                </tr>
              </thead>
              <tbody>
                {mockOrders.map((o) => (
                  <tr key={o.id}>
                    <td>{o.id}</td>
                    <td>{o.customerName}</td>
                    <td>{o.itemsCount}</td>
                    <td>R{o.total.toFixed(2)}</td>
                    <td>
                      <span className={`status-badge ${o.status}`}>
                        {o.status}
                      </span>
                    </td>
                    <td>{o.createdAt}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}

          {tab === "tasks" && (
            <table className="data-table">
              <thead>
                <tr>
                  <th>Title</th>
                  <th>Priority</th>
                  <th>Status</th>
                  <th>Created</th>
                </tr>
              </thead>
              <tbody>
                {mockTasks.map((t) => (
                  <tr key={t.id}>
                    <td>{t.title}</td>
                    <td>
                      <span
                        className={`badge-priority badge-priority-${t.priority}`}
                      >
                        {t.priority}
                      </span>
                    </td>
                    <td>
                      <span className={`status-badge ${t.status}`}>
                        {t.status.replace("_", " ")}
                      </span>
                    </td>
                    <td>{t.createdAt}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>
    </div>
  );
}
