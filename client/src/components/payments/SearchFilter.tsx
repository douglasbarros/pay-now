import { useState } from "react";

export interface FilterOptions {
  search: string;
  status: string;
  sortBy: string;
}

interface SearchFilterProps {
  filters: FilterOptions;
  onFilterChange: (filters: FilterOptions) => void;
  onReset: () => void;
}

export default function SearchFilter({
  filters,
  onFilterChange,
  onReset,
}: SearchFilterProps) {
  return (
    <div
      style={{
        padding: "1.5rem",
        backgroundColor: "#f9fafb",
        borderRadius: "0.5rem",
        marginBottom: "1.5rem",
      }}
    >
      <div
        style={{
          display: "grid",
          gridTemplateColumns: "repeat(auto-fit, minmax(200px, 1fr))",
          gap: "1rem",
          marginBottom: "1rem",
        }}
      >
        {/* Search Input */}
        <div>
          <label
            style={{
              display: "block",
              fontSize: "0.875rem",
              fontWeight: "500",
              color: "#374151",
              marginBottom: "0.25rem",
            }}
          >
            Search
          </label>
          <input
            type="text"
            placeholder="Search by name or ID..."
            value={filters.search}
            onChange={(e) =>
              onFilterChange({ ...filters, search: e.target.value })
            }
            style={{
              width: "100%",
              padding: "0.5rem 0.75rem",
              border: "1px solid #d1d5db",
              borderRadius: "0.375rem",
              fontSize: "0.875rem",
              outline: "none",
              transition: "all 0.2s",
            }}
            onFocus={(e) => {
              e.target.style.borderColor = "#667eea";
              e.target.style.boxShadow = "0 0 0 3px rgba(102, 126, 234, 0.1)";
            }}
            onBlur={(e) => {
              e.target.style.borderColor = "#d1d5db";
              e.target.style.boxShadow = "none";
            }}
          />
        </div>

        {/* Status Filter */}
        <div>
          <label
            style={{
              display: "block",
              fontSize: "0.875rem",
              fontWeight: "500",
              color: "#374151",
              marginBottom: "0.25rem",
            }}
          >
            Status
          </label>
          <select
            value={filters.status}
            onChange={(e) =>
              onFilterChange({ ...filters, status: e.target.value })
            }
            style={{
              width: "100%",
              padding: "0.5rem 0.75rem",
              border: "1px solid #d1d5db",
              borderRadius: "0.375rem",
              fontSize: "0.875rem",
              outline: "none",
              backgroundColor: "#fff",
              cursor: "pointer",
              transition: "all 0.2s",
            }}
            onFocus={(e) => {
              e.target.style.borderColor = "#667eea";
              e.target.style.boxShadow = "0 0 0 3px rgba(102, 126, 234, 0.1)";
            }}
            onBlur={(e) => {
              e.target.style.borderColor = "#d1d5db";
              e.target.style.boxShadow = "none";
            }}
          >
            <option value="">All Status</option>
            <option value="PROCESSED">Processed</option>
            <option value="PENDING">Pending</option>
            <option value="FAILED">Failed</option>
          </select>
        </div>

        {/* Sort By */}
        <div>
          <label
            style={{
              display: "block",
              fontSize: "0.875rem",
              fontWeight: "500",
              color: "#374151",
              marginBottom: "0.25rem",
            }}
          >
            Sort By
          </label>
          <select
            value={filters.sortBy}
            onChange={(e) =>
              onFilterChange({ ...filters, sortBy: e.target.value })
            }
            style={{
              width: "100%",
              padding: "0.5rem 0.75rem",
              border: "1px solid #d1d5db",
              borderRadius: "0.375rem",
              fontSize: "0.875rem",
              outline: "none",
              backgroundColor: "#fff",
              cursor: "pointer",
              transition: "all 0.2s",
            }}
            onFocus={(e) => {
              e.target.style.borderColor = "#667eea";
              e.target.style.boxShadow = "0 0 0 3px rgba(102, 126, 234, 0.1)";
            }}
            onBlur={(e) => {
              e.target.style.borderColor = "#d1d5db";
              e.target.style.boxShadow = "none";
            }}
          >
            <option value="date-desc">Newest First</option>
            <option value="date-asc">Oldest First</option>
            <option value="name-asc">Name (A-Z)</option>
            <option value="name-desc">Name (Z-A)</option>
          </select>
        </div>
      </div>

      {/* Reset Button */}
      <button
        onClick={onReset}
        style={{
          padding: "0.5rem 1rem",
          border: "1px solid #d1d5db",
          borderRadius: "0.375rem",
          fontSize: "0.875rem",
          fontWeight: "500",
          color: "#374151",
          backgroundColor: "#fff",
          cursor: "pointer",
          transition: "all 0.2s",
        }}
        onMouseEnter={(e) => {
          e.currentTarget.style.backgroundColor = "#f3f4f6";
        }}
        onMouseLeave={(e) => {
          e.currentTarget.style.backgroundColor = "#fff";
        }}
      >
        Reset Filters
      </button>
    </div>
  );
}
