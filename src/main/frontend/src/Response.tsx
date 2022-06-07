interface Sort {
    unsorted: boolean;
    sorted: boolean;
    empty: boolean;
}

interface Pageable {
    paged: boolean;
    pageNumber: number;
    offset: number;
    pageSize: number;
    unpaged: boolean;
    sort: Sort;
}

interface SearchData<T> {
    number: number;
    last: boolean;
    numberOfElements: number;
    size: number;
    totalPages: number;
    pageable: Pageable;
    sort: Sort;
    content: T[];
    first: boolean;
    totalElements: number;
    empty: boolean;
}

export interface SearchResponse<T> {
    search: SearchData<T>;
    success: boolean;
}
