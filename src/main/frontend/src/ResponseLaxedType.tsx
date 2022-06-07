
export type ResponseLaxedType<T extends object> = {
    [key in keyof T]: T[key] extends Date ? string : T[key] extends number | boolean ? T[key] : T[key] extends object ? ResponseLaxedType<T[key]> : string
}

export type ReponseLaxedTypeTransformer<T extends object> = {
    [key in keyof T]: (val: ResponseLaxedType<T>[key]) => T[key]
}