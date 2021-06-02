.onLoad <- function(libname, pkgname) {
  cat("")
  rJava::.jpackage(pkgname, lib.loc = libname)
  rJava::.jaddClassPath(dir(file.path(getwd(), "java"), full.names = TRUE))
}