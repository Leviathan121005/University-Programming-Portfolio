#' Preprocess Data and Label for Binary SVM Classifier
#'
#' Process data by mapping character features to numeric features, removing rows with NA values,
#' removing character feature columns that were not mapped, and adjusting label from binary to 1 and -1.
#'
#' @param data A data frame.
#' @param transform A list which maps column names to levels for character into numeric feature conversion.
#' @param label.col An index or name of column of the binary label.
#'
#' @examples
#' data = data.frame(weather = c("Rainy", "Sunny", "Sunny"),
#'                   temp = c("Cold", "Mild", "Hot"), play = c(1, 0, 1))
#' transform = list(weather = c("Rainy", "Sunny"), temp = c("Cold", "Mild", "Hot"))
#' data = preprocess(data, transform, 3)
#'
#' @return A clean numerical data frame of features and label.
#'
#' @import assertthat
#'
#' @export
preprocess = function(data, transform = NULL, label.col = NULL) {
  # Input validations.
  assert_that(is.data.frame(data), msg = "data must be a data frame.")
  if (!is.null(transform)) {
    assert_that(is.list(transform), msg = "transform must be a list.")
    assert_that(all(names(transform) %in% colnames(data)), msg = "There exist column(s) to transform that is not in data.")
  }
  if (!is.null(label.col)) {
    if (is.numeric(label.col)) {
      assert_that(length(label.col) == 1, msg = "Only one column can be the label for binary classification.")
      assert_that(label.col >= 1 && label.col <= ncol(data), msg = "label.col index is out of bounds.")
    }
    else if (is.character(label.col)) {
      assert_that(length(label.col) == 1, msg = "Only one column can be the label for binary classification.")
      assert_that(label.col %in% colnames(data), msg = "label.col does not exist in data")
    }
    else {
      stop("label.col must be a column name or column index.")
    }
  }

  # Map character feature columns to numeric feature columns.
  for (col in names(transform)) {
    data[, col] = as.numeric(factor(data[, col], levels = transform[[col]]))
    if (length(transform[[col]]) == 2) {
      data[, col] = data[, col] - 1
    }
  }

  # Convert binary 0 and 1 to -1 and 1.
  if (!is.null(label.col)) {
    data[which(data[, label.col] == 0), label.col] = -1
  }

  # Remove rows with NA and remove columns that are not numeric.
  data = data[which(rowSums(is.na(data)) == 0), which(sapply(data, is.numeric))]

  # Name unnamed columns.
  unnamed.columns = which(colnames(data) == "" | is.na(colnames(data)))
  colnames(data)[unnamed.columns] = paste("feature", unnamed.columns, sep = ".")

  return(data)
}

