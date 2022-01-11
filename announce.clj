#!/usr/bin/env bb
(ns announce
  (:require [clojure.string :refer [replace-first trim]])
  (:require [clojure.java.shell :refer [sh]]))

(def usage "Usage:

    announce on
    announce off
    announce status")

(defn expand-home [s]
  (if (.startsWith s "~")
    (replace-first s "~" (System/getProperty "user.home"))
    s))

(defn set-announce-the-time-pref
  []
  (sh "defaults" "write"  (expand-home "~/Library/Preferences/com.apple.speech.synthesis.general.prefs") "TimeAnnouncementPrefs" "-dict-add" "TimeAnnouncementsEnabled" "-bool" "YES"))


(defn start-speech-synthesis-server
  []
  (let [uid (trim (:out (sh "id" "-u")))]
    (sh "launchctl" "kickstart" (str "gui/" uid " /com.apple.speech.synthesisserver"))))


(defn turn-on
  []
  (set-announce-the-time-pref)
  (start-speech-synthesis-server))

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