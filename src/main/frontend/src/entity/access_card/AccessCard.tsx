export enum AccessCardStatus {
    AVAILABLE = 'AVAILABLE',
    IN_USE = 'IN_USE',
    BLOCKED = 'BLOCKED',
    IN_USE_AND_BLOCKED = 'IN_USE_AND_BLOCKED',
    CANCELLED = 'CANCELLED'
}

export interface AccessCard {
    code: string;
    createdBy: number;
    created: Date;
    modified: Date;
    modifiedBy: number;
    id: number;
    accessCardStatus: AccessCardStatus;
}