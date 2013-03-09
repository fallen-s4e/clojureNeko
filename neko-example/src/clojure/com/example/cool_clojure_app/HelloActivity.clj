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
  (set-on-click (get-run-button this) (fn[_]
                                        (let [input (read-string (.toString (.getText (get-input-edit-text this))))
                                              fun @(resolve (first input)) ;; first elem in a list must be a symbol
                                              args (rest input)]
                                          (set-result this (str (apply fun args))))))
  (set-on-click (get-clear-button this) (fn[_]
                                          (set-result this ""))))

(defn -onCreate
  [this bundle]
  ;; (print (read-string "(+ 2 2)"))
  ;; (apply map [inc [1 2 3]])
  (doto this
    (.superOnCreate bundle)
    (.setContentView R$layout/main))
  (init-buttons this)
  )
;;  (. (.findViewById this R$id/resultView) setText "!!!"))