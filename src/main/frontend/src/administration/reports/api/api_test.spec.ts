/**
 * Biblivre REST API
 * No description provided (generated by Swagger Codegen https://github.com/swagger-api/swagger-codegen)
 *
 * OpenAPI spec version: 0.0.1
 * 
 *
 * NOTE: This file is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the file manually.
 */

import * as api from "./api"
import { Configuration } from "./configuration"

const config: Configuration = {}

describe("ReportApi", () => {
  let instance: api.ReportApi
  beforeEach(function() {
    instance = new api.ReportApi(config)
  });

  test("addReport", () => {
    return expect(instance.addReport({})).resolves.toBe(null)
  })
  test("deleteFills", () => {
    const reportId: number = 56
    return expect(instance.deleteFills(reportId, {})).resolves.toBe(null)
  })
  test("deleteReport", () => {
    const reportId: number = 56
    return expect(instance.deleteReport(reportId, {})).resolves.toBe(null)
  })
  test("fillReport", () => {
    const body: Array<ReportFillParameter> = undefined
    const reportId: number = 56
    return expect(instance.fillReport(body, reportId, {})).resolves.toBe(null)
  })
  test("getReport", () => {
    const reportId: number = 56
    return expect(instance.getReport(reportId, {})).resolves.toBe(null)
  })
  test("getReports", () => {
    return expect(instance.getReports({})).resolves.toBe(null)
  })
  test("updateReport", () => {
    const body: api.NameAndDescription = undefined
    const reportId: number = 56
    return expect(instance.updateReport(body, reportId, {})).resolves.toBe(null)
  })
})

describe("ReportFillApi", () => {
  let instance: api.ReportFillApi
  beforeEach(function() {
    instance = new api.ReportFillApi(config)
  });

  test("deleteFills", () => {
    const reportId: number = 56
    return expect(instance.deleteFills(reportId, {})).resolves.toBe(null)
  })
  test("deleteReportFill", () => {
    const reportId: number = 56
    const reportFillId: number = 56
    return expect(instance.deleteReportFill(reportId, reportFillId, {})).resolves.toBe(null)
  })
  test("getReportFill", () => {
    const reportId: number = 56
    const reportFillId: number = 56
    return expect(instance.getReportFill(reportId, reportFillId, {})).resolves.toBe(null)
  })
})
