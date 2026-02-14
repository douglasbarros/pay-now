interface PaginationProps {
  currentPage: number;
  totalPages: number;
  totalItems: number;
  itemsPerPage: number;
  onPageChange: (page: number) => void;
  onItemsPerPageChange: (itemsPerPage: number) => void;
}

export default function Pagination({
  currentPage,
  totalPages,
  totalItems,
  itemsPerPage,
  onPageChange,
  onItemsPerPageChange,
}: PaginationProps) {
  const startItem = (currentPage - 1) * itemsPerPage + 1;
  const endItem = Math.min(currentPage * itemsPerPage, totalItems);

  const getPageNumbers = () => {
    const pages: (number | string)[] = [];
    const maxVisible = 5;

    if (totalPages <= maxVisible) {
      for (let i = 1; i <= totalPages; i++) {
        pages.push(i);
      }
    } else {
      pages.push(1);

      if (currentPage > 3) {
        pages.push("...");
      }

      const start = Math.max(2, currentPage - 1);
      const end = Math.min(totalPages - 1, currentPage + 1);

      for (let i = start; i <= end; i++) {
        pages.push(i);
      }

      if (currentPage < totalPages - 2) {
        pages.push("...");
      }

      pages.push(totalPages);
    }

    return pages;
  };

  return (
    <div
      style={{
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
        padding: "1.5rem",
        backgroundColor: "#f9fafb",
        borderRadius: "0.5rem",
        marginTop: "1.5rem",
        flexWrap: "wrap",
        gap: "1rem",
      }}
    >
      {/* Items per page */}
      <div style={{ display: "flex", alignItems: "center", gap: "0.5rem" }}>
        <label
          style={{
            fontSize: "0.875rem",
            color: "#6b7280",
            fontWeight: "500",
          }}
        >
          Show:
        </label>
        <select
          value={itemsPerPage}
          onChange={(e) => onItemsPerPageChange(Number(e.target.value))}
          style={{
            padding: "0.375rem 0.5rem",
            border: "1px solid #d1d5db",
            borderRadius: "0.375rem",
            fontSize: "0.875rem",
            outline: "none",
            backgroundColor: "#fff",
            cursor: "pointer",
          }}
        >
          <option value={5}>5</option>
          <option value={10}>10</option>
          <option value={20}>20</option>
          <option value={50}>50</option>
        </select>
        <span style={{ fontSize: "0.875rem", color: "#6b7280" }}>
          Showing {startItem}-{endItem} of {totalItems}
        </span>
      </div>

      {/* Page numbers */}
      <div style={{ display: "flex", gap: "0.25rem", alignItems: "center" }}>
        {/* Previous button */}
        <button
          onClick={() => onPageChange(currentPage - 1)}
          disabled={currentPage === 1}
          style={{
            padding: "0.5rem 0.75rem",
            border: "1px solid #d1d5db",
            borderRadius: "0.375rem",
            backgroundColor: "#fff",
            cursor: currentPage === 1 ? "not-allowed" : "pointer",
            opacity: currentPage === 1 ? 0.5 : 1,
            fontSize: "0.875rem",
            fontWeight: "500",
            color: "#374151",
          }}
        >
          Previous
        </button>

        {/* Page numbers */}
        {getPageNumbers().map((page, index) =>
          page === "..." ? (
            <span
              key={`ellipsis-${index}`}
              style={{
                padding: "0.5rem 0.75rem",
                color: "#6b7280",
              }}
            >
              ...
            </span>
          ) : (
            <button
              key={page}
              onClick={() => onPageChange(page as number)}
              style={{
                padding: "0.5rem 0.75rem",
                border: "1px solid #d1d5db",
                borderRadius: "0.375rem",
                backgroundColor: currentPage === page ? "#667eea" : "#fff",
                color: currentPage === page ? "#fff" : "#374151",
                cursor: "pointer",
                fontSize: "0.875rem",
                fontWeight: "500",
                minWidth: "2.5rem",
              }}
              onMouseEnter={(e) => {
                if (currentPage !== page) {
                  e.currentTarget.style.backgroundColor = "#f3f4f6";
                }
              }}
              onMouseLeave={(e) => {
                if (currentPage !== page) {
                  e.currentTarget.style.backgroundColor = "#fff";
                }
              }}
            >
              {page}
            </button>
          ),
        )}

        {/* Next button */}
        <button
          onClick={() => onPageChange(currentPage + 1)}
          disabled={currentPage === totalPages}
          style={{
            padding: "0.5rem 0.75rem",
            border: "1px solid #d1d5db",
            borderRadius: "0.375rem",
            backgroundColor: "#fff",
            cursor: currentPage === totalPages ? "not-allowed" : "pointer",
            opacity: currentPage === totalPages ? 0.5 : 1,
            fontSize: "0.875rem",
            fontWeight: "500",
            color: "#374151",
          }}
        >
          Next
        </button>
      </div>
    </div>
  );
}
