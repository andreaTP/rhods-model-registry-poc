#!/usr/bin/env python3

import numpy as np
from sklearn.svm import SVC

X = np.array([[0, 0], [10, 10], [20, 20], [30, 30]])
y = np.array([0, 1000, 2000, 3000])
clf = SVC()
clf.fit(X, y)

# store the model
import joblib
joblib.dump(clf, "example.joblib")
