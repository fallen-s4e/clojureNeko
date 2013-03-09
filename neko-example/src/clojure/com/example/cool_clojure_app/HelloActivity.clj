(ns com.example.cool_clojure_app.HelloActivity
  (:import [com.example.cool_clojure_app R$layout])
  (:gen-class :main false
              :extends android.app.Activity
              :exposes-methods {onCreate superOnCreate}))

(defn -onCreate
  [this bundle]
  (doto this
    (.superOnCreate bundle)
    (.setContentView R$layout/main)))