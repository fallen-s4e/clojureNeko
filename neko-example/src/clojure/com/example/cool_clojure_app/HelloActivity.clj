(ns com.example.cool_clojure_app.HelloActivity
  (:import [com.example.cool_clojure_app R$layout])
  (:use clojure.core)
  (:gen-class :main false
              :extends android.app.Activity
              :exposes-methods {onCreate superOnCreate}))


(defn -onCreate
  [this bundle]
;  (print (read-string "(+ 2 2)"))
;  (apply map [inc [1 2 3]])
  (doto this
    (.superOnCreate bundle)
    (.setContentView R$layout/main)))