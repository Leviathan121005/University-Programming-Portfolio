## ----include = FALSE----------------------------------------------------------
knitr::opts_chunk$set(collapse = TRUE, comment = "#>", cache = FALSE, 
                      autodep = FALSE, cache.comments = FALSE,
                      fig.width = 8, fig.height = 5)

suppressPackageStartupMessages(library(BinarySVMXHT))


## ----1, include = TRUE--------------------------------------------------------
library(BinarySVMXHT)

dataset = student_depression_dataset
names(dataset)
head(dataset, 6)


## ----2, include = TRUE--------------------------------------------------------
plot_hist(dataset, "Gender")
plot_hist(dataset, "Age")
plot_hist(dataset, "City")


## ----3, eval = FALSE----------------------------------------------------------
# lapply(names(dataset), function(x) {
#   plot_hist(dataset, x)
# })
# 

## ----4, include = TRUE--------------------------------------------------------
dataset = dataset[, -which(names(dataset) %in% c("id", "Profession", "Job.Satisfaction", "Work.Pressure"))]


## ----5, include = TRUE--------------------------------------------------------
# Create a list which maps the character feature columns to numeric.
mapping = list(Gender = c("Female", "Male"),
                 Sleep.Duration = c("Less than 5 hours", "5-6 hours", "7-8 hours", "More than 8 hours", "Others"),
                 Dietary.Habits = c("Unhealthy", "Moderate", "Healthy"),
                 Have.you.ever.had.suicidal.thoughts.. = c("No", "Yes"),
                 Family.History.of.Mental.Illness = c("No", "Yes"))

dataset = preprocess(dataset, mapping, "Depression")

head(dataset, 6)


## ----6, include = TRUE--------------------------------------------------------
data = dataset[, 1:11]
label = dataset[, 12]
linear.model = svm(data[1:250, ], label[1:250])
linear.model


## ----7, include = TRUE--------------------------------------------------------
linear.model.partial = svm(data, label, n = 250, features = names(data)[1:8])
linear.model.partial


## ----8, include = TRUE--------------------------------------------------------
poly.model = svm(data, label, type = "polynomial", n = 250)

rbf.model = svm(data, label, type = "rbf", n = 250)


## ----9, include = TRUE--------------------------------------------------------
random.test.index = sample(1:nrow(data), size = 250, replace = FALSE)

predict(linear.model, data[random.test.index, ])
test_accuracy(linear.model, data[random.test.index, ], label[random.test.index])

predict(linear.model.partial, data[random.test.index, ])
test_accuracy(linear.model.partial, data[random.test.index, ], label[random.test.index])

predict(poly.model, data[random.test.index, ])
test_accuracy(poly.model, data[random.test.index, ], label[random.test.index])

predict(rbf.model, data[random.test.index, ])
test_accuracy(rbf.model, data[random.test.index, ], label[random.test.index])


## ----10, include = TRUE-------------------------------------------------------
select_features(data, label, n =  8, show = TRUE)
select_features(data, label, min = 0.2)


## ----11, include = TRUE-------------------------------------------------------
random.train.index = sample(1:nrow(data), size = 250, replace = FALSE)

tune_parameters(data[random.train.index, ], label[random.train.index], 
               data[random.test.index, ], label[random.test.index], type = "linear", C = c(1, 10, 100))

tune_parameters(data[random.train.index, ], label[random.train.index], 
               data[random.test.index, ], label[random.test.index], type = "polynomial", C = c(1, 10, 100), degree = c(2, 3))

tune_parameters(data[random.train.index, ], label[random.train.index], 
               data[random.test.index, ], label[random.test.index], type = "rbf", C = c(1, 10, 100), gamma = c(0.05, 0.1, 0.2))


## ----12, include = TRUE-------------------------------------------------------
random.test.index.2 = sample(1:nrow(data), size = 250, replace = FALSE)

linear.model.tuned = svm(data, label, type = "linear", n = 250, C = 10)
test_accuracy(linear.model.tuned, data[random.test.index.2, ], label[random.test.index.2])

poly.model.tuned = svm(data, label, type = "polynomial", n = 250, degree = 3)
test_accuracy(poly.model.tuned, data[random.test.index.2, ], label[random.test.index.2])

rbf.model.tuned = svm(data, label, type = "rbf", n = 250, C = 1, gamma = 0.2)
test_accuracy(rbf.model.tuned, data[random.test.index.2, ], label[random.test.index.2])


## ----13, include = TRUE-------------------------------------------------------
linear.model.z.test = hypo_test(linear.model.tuned, data[random.test.index.2, ], label[random.test.index.2], 0.80, alternative = "greater", method = "z")
linear.model.lrt = hypo_test(linear.model.tuned, data[random.test.index.2, ], label[random.test.index.2], 0.80, alternative = "greater", method = "lrt")
linear.model.z.test$status
linear.model.lrt$status

rbf.model.z.test = hypo_test(rbf.model.tuned, data[random.test.index.2, ], label[random.test.index.2], 0.80, alternative = "greater", method = "z")
rbf.model.lrt = hypo_test(rbf.model.tuned, data[random.test.index.2, ], label[random.test.index.2], 0.80, alternative = "greater", method = "lrt")
rbf.model.z.test$status
rbf.model.lrt$status


## ----14, include = TRUE-------------------------------------------------------
visualize_ht(linear.model.z.test)
visualize_ht(linear.model.z.test, type = "II")
visualize_ht(linear.model.lrt)
visualize_ht(rbf.model.z.test)
visualize_ht(rbf.model.z.test, type = "II")
visualize_ht(rbf.model.lrt)


