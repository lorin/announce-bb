#!/usr/bin/env bb
(ns announce)

(def usage "Usage:

    announce on
    announce off
    announce status")

(defn turn-on
  []
  (println "turn it on!"))

(defn turn-off
  []
  (println "turn it off!"))

(defn check-status
  []
  (println "check the status!"))

(defn main
  [args]
  (if-let [[arg] args]
    (case arg
      "on" (turn-on)
      "off" (turn-off)
      "status" (check-status)
      (println usage))
    (println usage)))

(main *command-line-args*)