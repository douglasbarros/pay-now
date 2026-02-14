"use client";

import { useState, useEffect } from "react";
import { paymentApi, PaymentResponse, PageResponse } from "@/lib/api";
import PaymentCard from "./PaymentCard";
import SearchFilter, { FilterOptions } from "./SearchFilter";
import Pagination from "./Pagination";

export default function PaymentsListPage() {
  const [payments, setPayments] = useState<PaymentResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [totalItems, setTotalItems] = useState(0);

  const [filters, setFilters] = useState<FilterOptions>({
    search: "",
    status: "All",
    sortBy: "date",
  });

  // Fetch payments with pagination
  useEffect(() => {
    const fetchPayments = async () => {
      try {
        setLoading(true);
        setError(null);

        // Backend uses 0-indexed pages
        const pageResponse: PageResponse<PaymentResponse> =
          await paymentApi.getPaginated(currentPage - 1, itemsPerPage);

        setPayments(pageResponse.content);
        setTotalPages(pageResponse.totalPages);
        setTotalItems(pageResponse.totalElements);
      } catch (err) {
        console.error("Failed to fetch payments:", err);
        setError("Failed to load payments. Please try again later.");
      } finally {
        setLoading(false);
      }
    };

    fetchPayments();
  }, [currentPage, itemsPerPage]);

  // Apply filters and sorting (client-side for now within current page)
  const filteredPayments = payments.filter((payment) => {
    // Apply search filter
    if (filters.search) {
      const searchLower = filters.search.toLowerCase();
      const matchesSearch =
        `${payment.firstName} ${payment.lastName}`
          .toLowerCase()
          .includes(searchLower) ||
        payment.id.toLowerCase().includes(searchLower) ||
        payment.maskedCardNumber.toLowerCase().includes(searchLower) ||
        payment.zipCode.toLowerCase().includes(searchLower);

      if (!matchesSearch) return false;
    }

    // Apply status filter
    if (filters.status && filters.status !== "All") {
      if (payment.status !== filters.status) return false;
    }

    return true;
  });

  // Apply sorting
  const sortedPayments = [...filteredPayments].sort((a, b) => {
    if (filters.sortBy === "date") {
      return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
    } else {
      const nameA = `${a.firstName} ${a.lastName}`;
      const nameB = `${b.firstName} ${b.lastName}`;
      return nameA.localeCompare(nameB);
    }
  });

  const handleFilterChange = (newFilters: FilterOptions) => {
    setFilters(newFilters);
    setCurrentPage(1); // Reset to first page when filters change
  };

  const handleReset = () => {
    setFilters({
      search: "",
      status: "All",
      sortBy: "date",
    });
    setCurrentPage(1);
  };

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  const handleItemsPerPageChange = (newItemsPerPage: number) => {
    setItemsPerPage(newItemsPerPage);
    setCurrentPage(1);
  };

  return (
    <div style={{ maxWidth: "1200px", margin: "0 auto", padding: "2rem 1rem" }}>
      {/* Header */}
      <div style={{ marginBottom: "2rem" }}>
        <h1
          style={{
            fontSize: "2rem",
            fontWeight: "bold",
            color: "#1f2937",
            marginBottom: "0.5rem",
          }}
        >
          Payments
        </h1>
        <p style={{ color: "#6b7280", fontSize: "1rem" }}>
          View and manage all payment transactions
        </p>
      </div>

      {/* Search and Filter */}
      <SearchFilter
        filters={filters}
        onFilterChange={handleFilterChange}
        onReset={handleReset}
      />

      {/* Loading State */}
      {loading && (
        <div
          style={{
            textAlign: "center",
            padding: "4rem",
            color: "#6b7280",
            fontSize: "1.125rem",
          }}
        >
          Loading payments...
        </div>
      )}

      {/* Error State */}
      {error && (
        <div
          style={{
            backgroundColor: "#fef2f2",
            border: "1px solid #fecaca",
            borderRadius: "0.5rem",
            padding: "1rem",
            marginTop: "1rem",
            color: "#991b1b",
          }}
        >
          <strong>Error:</strong> {error}
        </div>
      )}

      {/* Empty State */}
      {!loading && !error && sortedPayments.length === 0 && (
        <div
          style={{
            textAlign: "center",
            padding: "4rem",
            backgroundColor: "#f9fafb",
            borderRadius: "0.5rem",
            marginTop: "1.5rem",
          }}
        >
          <p
            style={{
              color: "#6b7280",
              fontSize: "1.125rem",
              marginBottom: "0.5rem",
            }}
          >
            No payments found
          </p>
          <p style={{ color: "#9ca3af", fontSize: "0.875rem" }}>
            {totalItems === 0
              ? "Try creating your first payment"
              : "Try adjusting your search or filters"}
          </p>
        </div>
      )}

      {/* Payment List */}
      {!loading && !error && sortedPayments.length > 0 && (
        <>
          <div
            style={{
              display: "grid",
              gap: "1rem",
              marginTop: "1.5rem",
            }}
          >
            {sortedPayments.map((payment) => (
              <PaymentCard key={payment.id} payment={payment} />
            ))}
          </div>

          {/* Pagination */}
          {totalPages > 1 && (
            <Pagination
              currentPage={currentPage}
              totalPages={totalPages}
              totalItems={totalItems}
              itemsPerPage={itemsPerPage}
              onPageChange={handlePageChange}
              onItemsPerPageChange={handleItemsPerPageChange}
            />
          )}
        </>
      )}

      {/* Results Summary */}
      {!loading && !error && sortedPayments.length > 0 && (
        <div
          style={{
            textAlign: "center",
            marginTop: "1.5rem",
            color: "#6b7280",
            fontSize: "0.875rem",
          }}
        >
          {filters.search || filters.status !== "All"
            ? `Showing ${sortedPayments.length} of ${totalItems} payments`
            : `Total: ${totalItems} payments`}
        </div>
      )}
    </div>
  );
}
