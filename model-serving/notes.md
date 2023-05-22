

Minimal example

```python
import numpy as np
from sklearn.svm import SVC

X = np.array([[-1, -1], [-2, -1], [1, 1], [2, 1]])
y = np.array([1, 1, 2, 2])
clf = SVC()
clf.fit(X, y)

import joblib
joblib.dump(clf, 'last-basic-example.joblib')

print(clf.predict([[-0.8, -1]]))
print(clf.predict([[0.8, 1]]))
```
