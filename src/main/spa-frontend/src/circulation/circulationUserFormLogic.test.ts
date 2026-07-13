import { describe, expect, it } from 'vitest'

import { userFieldsFixture } from '../api-helpers/user-fields/fixtures'

import {
  buildSavePayload,
  createEmptyUserFormValues,
  formatFieldValueForDisplay,
  getListFieldOptions,
  parseServerFieldErrors,
  userToFormValues,
  validateUserFormValues,
} from './circulationUserFormLogic'

import type { User } from '../api-helpers/circulation/response-types'

const sampleUser: User = {
  id: 42,
  name: 'Maria Silva',
  type: 1,
  type_name: 'Padrão',
  loginId: 0,
  enrollment: '00042',
  status: 'active',
  createdBy: 1,
  modifiedBy: 1,
  created: '2024-01-01T10:00:00.000Z' as User['created'],
  modified: '2024-01-02T10:00:00.000Z' as User['modified'],
  fields: {
    email: 'maria@example.com',
    gender: '1',
    phone_cel: '',
    phone_home: '',
    phone_work: '',
    phone_work_extension: '',
    id_rg: '',
    id_cpf: '',
    address: '',
    address_number: '',
    address_complement: '',
    address_zip: '',
    address_city: '',
    address_state: '',
    birthday: '1990-05-10T00:00:00.000Z' as User['fields']['birthday'],
    obs: '',
  },
}

describe('circulationUserForm', () => {
  describe('createEmptyUserFormValues', () => {
    it('creates defaults for a new user', () => {
      expect(createEmptyUserFormValues(2)).toEqual({
        id: null,
        name: '',
        enrollment: '',
        type: 2,
        status: 'active',
        fields: {},
        photoData: null,
        photoPreviewUrl: null,
      })
    })
  })

  describe('userToFormValues', () => {
    it('maps user data into form values', () => {
      expect(userToFormValues(sampleUser)).toEqual({
        id: 42,
        name: 'Maria Silva',
        enrollment: '00042',
        type: 1,
        status: 'active',
        fields: {
          email: 'maria@example.com',
          gender: '1',
          birthday: '10/05/1990',
        },
        photoData: null,
        photoPreviewUrl: null,
      })
    })
  })

  describe('buildSavePayload', () => {
    it('builds create payload with id 0', () => {
      const values = {
        ...createEmptyUserFormValues(1),
        name: ' João ',
        fields: { email: 'joao@example.com' },
        photoData: 'abc123',
      }

      expect(buildSavePayload(values, false)).toEqual({
        id: 0,
        name: 'João',
        type: 1,
        status: 'active',
        photo_data: 'abc123',
        email: 'joao@example.com',
      })
    })

    it('builds save-as-new payload with id 0', () => {
      const values = {
        ...userToFormValues(sampleUser),
        photoData: null,
      }

      expect(buildSavePayload(values, true)).toEqual({
        id: 0,
        name: 'Maria Silva',
        type: 1,
        status: 'active',
        email: 'maria@example.com',
        gender: '1',
        birthday: '10/05/1990',
      })
    })
  })

  describe('validateUserFormValues', () => {
    it('returns required field errors', () => {
      const errors = validateUserFormValues(
        createEmptyUserFormValues(null),
        userFieldsFixture,
      )

      expect(errors).toMatchObject({
        name: 'field.error.required',
        type: 'field.error.required',
        email: 'field.error.required',
      })
    })

    it('rejects invalid user names', () => {
      const errors = validateUserFormValues(
        {
          ...createEmptyUserFormValues(1),
          name: 'Invalid:Name',
          fields: { email: 'user@example.com' },
        },
        userFieldsFixture,
      )

      expect(errors.name).toBe('circulation.error.invalid_user_name')
    })

    it('validates max length and date format', () => {
      const errors = validateUserFormValues(
        {
          ...createEmptyUserFormValues(1),
          name: 'Maria',
          fields: {
            email: 'user@example.com',
            phone_cel: 'x'.repeat(30),
            birthday: 'not-a-date',
          },
        },
        userFieldsFixture,
      )

      expect(errors.phone_cel).toBe('field.error.max_length:::25')
      expect(errors.birthday).toBe('field.error.date:::DD/MM/YYYY')
    })
  })

  describe('getListFieldOptions', () => {
    it('builds list options from legacy translation keys', () => {
      expect(getListFieldOptions('gender', 2)).toEqual([
        { value: '1', text: 'Masculino' },
        { value: '2', text: 'Feminino' },
      ])
    })
  })

  describe('formatFieldValueForDisplay', () => {
    it('returns a dash for empty values', () => {
      expect(formatFieldValueForDisplay(userFieldsFixture[0], undefined)).toBe(
        '-',
      )
    })

    it('formats list, boolean, and date values', () => {
      expect(formatFieldValueForDisplay(userFieldsFixture[1], '1')).toBe(
        'Masculino',
      )
      expect(
        formatFieldValueForDisplay(
          {
            ...userFieldsFixture[0],
            key: 'active',
            type: 'boolean',
          },
          'true',
        ),
      ).toBe('Sim')
      expect(
        formatFieldValueForDisplay(
          userFieldsFixture.find((field) => field.key === 'birthday')!,
          '1990-05-10T00:00:00.000Z',
        ),
      ).toBe('10/05/1990')
    })
  })

  describe('parseServerFieldErrors', () => {
    it('flattens server error objects', () => {
      expect(
        parseServerFieldErrors([
          { email: 'Email inválido' },
          { name: 'Nome inválido' },
        ]),
      ).toEqual({
        email: 'Email inválido',
        name: 'Nome inválido',
      })
    })
  })
})
