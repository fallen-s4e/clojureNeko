(ns kz.kaznu.base.serialization)

(defn seri[x]
  (if (instance? String x)
    (str "\"" x "\"")
    (str x)))

(defn deseri[x]
  (read-string x))