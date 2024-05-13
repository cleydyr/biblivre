export function getSchemaFromURL() {
  return window.location.pathname.split("/")[1] || "bcuniaodosaber";
}
