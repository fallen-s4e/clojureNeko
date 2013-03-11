(ns kz.kaznu.activities.HelloActivity
  (:import [kz.kaznu.activities R$layout])
  (:import [kz.kaznu.activities R$id])
  (:use clojure.core)
  (:use kz.kaznu.base.client)
  (:use kz.kaznu.base.serialization)
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

(defn interprete
  "expr is a parsed expression and the function returns evaluated expression. Cut version of eval"
  [ expr ]
  (let [fun @(resolve (first input)) ;; first elem in a list must be a symbol
        args (rest input)]
    (apply fun args)))

;; initializing
(defn init-buttons[this]
  (set-on-click (get-run-button this) (fn[_]
                                        (let [input (read-string (.toString (.getText (get-input-edit-text this))))]
                                          (set-result this (str (interprete input)))))
  (set-on-click (get-clear-button this) (fn[_]
                                          (set-result this ""))))


;; overridings
(defn -onCreate
  [this bundle]
  (doto this
    (.superOnCreate bundle)
    (.setContentView R$layout/main))
  (init-buttons this))

;; API
