(ns kz.kaznu.activities.HelloActivity
  (:import [kz.kaznu.activities R$layout])
  (:import [kz.kaznu.activities R$id])
  (:require neko.compilation)
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

(defn eval-new-thread
  "evaluates expressions in new thread, catching all the errors being in this namespace kz.kaznu.activities.HelloActivity"
  [ & exprs ]
  @(future
     (binding [*ns* (find-ns 'kz.kaznu.activities.HelloActivity)]
       ((kz.kaznu.base.client/catch-all apply)
        eval exprs))))

;; initializing
(defn init-buttons[this]
  (set-on-click (get-run-button this) (fn[_]
                                        (let [input (read-string (.toString (.getText (get-input-edit-text this))))]
                                          ;; (future (set-result this (str (eval input)))))))
                                          (set-result this (str (eval-new-thread input))))))
  (set-on-click (get-clear-button this) (fn[_]
                                          (.setText (get-input-edit-text this) "")
                                          (set-result this ""))))


;; overridings
(defn -onCreate
  [this bundle]
  (doto this
    (.superOnCreate bundle)
    (.setContentView R$layout/main))
  (init-buttons this)
  (neko.compilation/init this) ;; allowing dynamic compilation
  )

;; API
