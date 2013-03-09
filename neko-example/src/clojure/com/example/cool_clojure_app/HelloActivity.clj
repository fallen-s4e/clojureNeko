(ns com.example.cool_clojure_app.HelloActivity
  (:import [com.example.cool_clojure_app R$layout])
  (:import [com.example.cool_clojure_app R$id])
  (:use clojure.core)
  (:gen-class :main false
              :extends android.app.Activity
              :exposes-methods {onCreate superOnCreate}))

;; components accessors
(defn get-run-button ^android.widget.Button [this]
  (.findViewById this R$id/runButton))

(defn get-clear-button ^android.widget.Button [this]
  (.findViewById this R$id/clearButton))

(defn set-result [this string-value]
  (.setText (.findViewById this R$id/resultView) string-value))

(defn get-input-edit-text ^android.widget.TextView [this]
  (.findViewById this R$id/inputEditText))

;; utility methods
(defn set-on-click[button fun]
  (.setOnClickListener button
                       (proxy [ android.view.View$OnClickListener ]
                           []
                         (onClick [#^android.view.View view] (fun view)))))

;; initializing
(defn init-buttons[this]
  (comment 
  (set-on-click (get-run-button) (fn[x]
                                   (let [v (.toString (.getText (get-input-edit-text)))]
                                     (set-result (str v "!"))))))
  (set-result this "Hey World!"))

(defn -onCreate
  [this bundle]
  (print (read-string "(+ 2 2)"))
  (apply map [inc [1 2 3]])
  (.findViewById this R$id/resultView)
;;  (init-buttons this)
  (doto this
    (.superOnCreate bundle)
    (.setContentView R$layout/main)))