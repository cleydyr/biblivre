import {
    CriteriaWithPagination,
    EuiBadge,
    EuiBasicTable,
    EuiButton,
    EuiButtonEmpty,
    EuiFieldSearch,
    EuiFlexGroup,
    EuiFlexItem,
    EuiForm, EuiLink, EuiLinkColor, EuiSelect,
    EuiSpacer
} from '@elastic/eui';
import { useState } from 'react';

import { AccessCard, AccessCardStatus } from './Card';

const badge: Record<AccessCardStatus, [string, string]> = {
    AVAILABLE: ['success', 'Disponível'],
    BLOCKED: ['danger', 'Bloqueado'],
    IN_USE: ['warning', 'Em uso'],
    CANCELLED: ['default', 'Cancelado'],
    IN_USE_AND_BLOCKED: ['danger', 'Em uso e bloqueado']
}

export const statusRender = (accessCard: AccessCard) => {
    const [color, description] = badge[accessCard.accessCardStatus];

    return <EuiBadge color={color}>{description}</EuiBadge>
}

type AccessCardAction = (accessCard: AccessCard) => void

const changeAccessCardStatusWithCallback =
    (callback: AccessCardAction, newAccessCardStatus: AccessCardStatus) =>
        (accessCard: AccessCard) =>
            callback({ ...accessCard, accessCardStatus: newAccessCardStatus })

const renderFirstButton = (onItemChange: AccessCardAction) => (item: AccessCard) => {
    let [color, message, onClick]: [EuiLinkColor, string, AccessCardAction] = ['danger', 'Cancelar', () => { }];

    const changeStatus = (accessCardStatus: AccessCardStatus) => changeAccessCardStatusWithCallback(onItemChange, accessCardStatus)

    switch (item.accessCardStatus) {
        case AccessCardStatus.AVAILABLE:
        case AccessCardStatus.IN_USE:
            [color, message, onClick] = ['danger', 'Bloquear', changeStatus(AccessCardStatus.BLOCKED)]
            break
        case AccessCardStatus.BLOCKED:
            [color, message, onClick] = ['success', 'Desbloquear', changeStatus(AccessCardStatus.AVAILABLE)]
            break
        case AccessCardStatus.IN_USE_AND_BLOCKED:
            [color, message, onClick] = ['success', 'Desbloquear', changeStatus(AccessCardStatus.IN_USE)]
            break
        case AccessCardStatus.CANCELLED:
            [color, message, onClick] = ['success', 'Recuperar', changeStatus(AccessCardStatus.AVAILABLE)]
            break
    }

    return <EuiLink onClick={() => onClick(item)} color={color}>{message}</EuiLink>;
}

const renderSecondButton = (onItemChange: AccessCardAction, onItemDelete: AccessCardAction) => (item: AccessCard) => {
    const changeStatus = (accessCardStatus: AccessCardStatus) => changeAccessCardStatusWithCallback(onItemChange, accessCardStatus)

    let [color, message, onClick]: [EuiLinkColor, string, AccessCardAction] = ['danger', 'Cancelar', changeStatus(AccessCardStatus.CANCELLED)];

    if (item.accessCardStatus === AccessCardStatus.CANCELLED) {
        [color, message, onClick] = ['danger', 'Excluir', onItemDelete]
    }

    return <EuiLink onClick={() => onClick(item)} color={color}>{message}</EuiLink>;
}

// const accessCardActionsFunctions = (onItemChange: AccessCardAction) =>
//     [renderFirstButton, renderSecondButton]
//         .map(f => ({ render: f(accessCardActions) }))

type AnyStatusFilter = {
    value: 'any'
    text: 'any'
}

type TypeFilter<T> = {
    value: T
    text: T
}

type AccessCardStatusFilterOrAny = TypeFilter<AccessCardStatus> | AnyStatusFilter

export const filterOptions: AccessCardStatusFilterOrAny[] = [
    {
        value: 'any',
        text: 'any'
    },
    ...Object.values(AccessCardStatus).map((key) => ({
        value: key,
        text: key
    }))
]

type AccessCardScreenProps = {
    items: AccessCard[]
    onItemChange: AccessCardAction
    onItemDelete: AccessCardAction
}

const AccessCardScreen = (props: AccessCardScreenProps) => {
    const {
        items,
        onItemChange,
        onItemDelete
    } = props;

    const [pageIndex, setPageIndex] = useState(0)

    const [pageSize, setPageSize] = useState(10)

    const [query, setQuery] = useState('');

    const [accessCardStatusFilter, setAccessCardStatusFilter] = useState('any')

    const filteredItems = items
        .filter(item => item.code.includes(query))
        .filter(item => accessCardStatusFilter === 'any' || item.accessCardStatus === accessCardStatusFilter);

    const displayedItems = filteredItems.slice(pageIndex * pageSize, pageIndex * pageSize + pageSize)

    return (
        <div>
            <EuiForm>
                <EuiFlexGroup>
                    <EuiFlexItem>
                        <EuiFieldSearch
                            value={query}
                            onChange={(event) => setQuery(event.target.value)}
                            fullWidth
                            placeholder='Código do cartão'
                            append={<EuiButtonEmpty>
                                <EuiSelect
                                    options={filterOptions}
                                    onChange={(event) => setAccessCardStatusFilter(event.target.value)}
                                />
                            </EuiButtonEmpty>} />
                    </EuiFlexItem>
                    <EuiFlexItem grow={false}>
                        <EuiButton>Filtrar</EuiButton>
                    </EuiFlexItem>
                    <EuiSpacer></EuiSpacer>
                </EuiFlexGroup>
            </EuiForm>
            <EuiSpacer />
            <EuiBasicTable
                pagination={{
                    pageIndex,
                    pageSize,
                    totalItemCount: filteredItems.length
                }}
                onChange={(criteria: CriteriaWithPagination<AccessCard>) => {
                    setPageIndex(criteria.page.index);
                    setPageSize(criteria.page.size);
                }}
                items={displayedItems}
                columns={[
                    {
                        field: 'code',
                        name: 'Código',
                    },
                    {
                        name: 'Estado',
                        render: statusRender
                    },
                    {
                        actions: [
                            {
                                render: renderFirstButton(onItemChange)
                            },
                            {
                                render: renderSecondButton(onItemChange, onItemDelete)
                            }
                        ]
                    }
                ]}
            >

            </EuiBasicTable>
        </div>
    );
}


export default AccessCardScreen