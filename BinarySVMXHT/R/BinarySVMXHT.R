#' BinarySVMXHT: Create Support Vector Machine (SVM) Binary Classification Model with Hypothesis Testing Validation
#'
#' The BinarySVMXHT package provides functionality for:
#' \itemize{
#'  \item Structured binary support vector machines (SVM) implementation
#'  \item Hypothesis testing of models' accuracy.
#'}
#' @section Binary SVM implementation:
#' \itemize{
#'  \item The `plot_hist()` function allows users to visualize distribution of data
#'  \item The `preprocess()` function allows users to convert character data to numeric and clean the data for SVM implementation.
#'  \item The `select_features()` function allows users to select meaningful features by analyzing the correlations between features and the binary label.
#'  \item The `svm()` function allows users to train a binary SVM model with linear, polynomial, or RBF kernels.
#'  \item The `predict()` function allows users to use the trained SVM model to predict label from feature data.
#'  \item The `test_accuracy()` function allows users to evaluate the accuracy between prediction and actual label.
#'  \item The `tune_parameters()` function allows users to compare performance of different parameters for the SVM model.
#'}
#'
#' @section Accuracy Hypothesis Testing:
#' \itemize{
#'  \item The `hypo_test()` function allows users to conduct Z-test or likelihood ratio test (LRT) on the models' accuracy.
#'  \item The `visualize_ht()` visualizes the hypothesis testing result from `hypo_test()` to help users understand the test result.
#' }
#'
#' @name BinarySVMXHT
NULL
