.onLoad <- function(libname, pkgname) {
  writeLines("Calling on load")
  writeLines(file.path(getwd()))
  writeLines(dir(file.path(getwd())))
  writeLines(dir(file.path(getwd(), "java")))
  rJava::.jpackage(pkgname, lib.loc = libname)
  rJava::.jaddClassPath(dir(file.path(getwd(), "java"), full.names = TRUE))
}