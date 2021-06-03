PACKAGE_NAME=rJavaPackageExample
# Path to subproject. If there are more subprojects, more variables like this
# have to be added.
# We require each subproject to contain a Makefile with `build` and `clean`
# targets defined: `build` builds subprojects' artefacts, and `clean` removes
# files derived from source code. We also require building to include running 
# the tests.
JAVA_PROJECT_PATH=../../java

# Directory where build package should be placed.
BUILD_TARGET=target
# Place where the produced Jar files should go.
JAVA_BUILD_TARGET=inst/java

all: build

build: $(BUILD_TARGET) tests docs

# Create portable bundle package ready to be installed.
$(BUILD_TARGET): build-subprojects
	rm -rf $(JAVA_BUILD_TARGET)
	mkdir -p $(JAVA_BUILD_TARGET)
	cp $(JAVA_PROJECT_PATH)/target/*.jar $(JAVA_BUILD_TARGET)
	mkdir -p $(BUILD_TARGET)
	Rscript -e "devtools::build(path = \"$(BUILD_TARGET)\")"

# Build subprojects. We assume that testing is a part of their building process.
build-subprojects:
	$(MAKE) -C $(JAVA_PROJECT_PATH) build

# Run all tests. Tests in subprojects are not run explicitly because we assume
# that building them requires them to pass tests anyway.
test: $(BUILD_TARGET)
	Rscript run_all_tests.R

# Generate documentation.
docs:
	Rscript -e "devtools::document()"

# Check the code and package structure for common problems; run tests.
# The number of ERRORs and WARNINGs should be zero. Ideally, the number of
# NOTE's also should be zero. Currently there's one NOTE that says that the
# paths to some of the files are too long (see README).
check: build
	Rscript -e "devtools::check()"

# Install the package in the system.
install: test docs
	R CMD INSTALL .

# Uninstall the package.
uninstall:
	R CMD REMOVE $(PACKAGE_NAME)

clean: clean-subprojects
	rm -rf man NAMESPACE *.tar.gz
	rm -rf $(JAVA_BUILD_TARGET)
	rm -rf $(BUILD_TARGET)

clean-subprojects:
	$(MAKE) -C $(JAVA_PROJECT_PATH) clean
