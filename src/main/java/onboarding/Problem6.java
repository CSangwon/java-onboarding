package onboarding;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 1. 제약사항 확인
 *   -> 1) forms 길이 체크 (1~10000 인지 확인)
 *   -> 2) 이메일 형식 체크 + 닉네임 길이 체크 + 닉네임 한글만 있는지 체크
 *       =>@뒤에 email.com 이 오는지 + 이메일 길이가 11~20인지 + 닉네임 길이가 1~20 인지 확인 + 유니코드로 가 ~ 힣까지 범위 체크
 * 2. 로직 생각
 *   -> 1)   (1) 모든 단어를 돌면서 길이가 2인 패턴을 찾음 (n)개 ( 2이상을 찾을 필요가 없는 이유 : 어차피 3, 4에도 2가 포함되어있음)
 *               HashSet을 3개 선언(전체 패턴, 중복패턴, 찾고있는 단어의 패턴)
 *               찾고있는 단어의 패턴에 모든 패턴을 넣은 후 전체패턴과 겹치는 것이 있는지 확인 겹칠 경우 중복 패턴에 추가
 *
 *   -> 2) 중복패턴에 저장된 패턴을 kmp알고리즘으로 패턴을 체크함
 *       => kmp : O(N)
 */
public class Problem6 {

    private static final String EMAIL_PATTERN = "email.com";

    public static List<String> solution(List<List<String>> forms) {
        validate(forms);
        List<String> answer = new ArrayList<>(getOverlapEmailsByNickname(findPattern(forms), forms));
        Collections.sort(answer);

        return answer;
    }
    public static Set<String> getOverlapEmailsByNickname(Set<String> overlapPattern, List<List<String>> forms) {
        Set<String> emails = new HashSet<>();
        for (String pattern : overlapPattern) {
            int[] kmpTable = makeKmpTable(pattern);
            for (List<String> form : forms) {
                if (form.get(1).length() != 1 && doKMP(form.get(1), pattern, kmpTable)){
                    emails.add(form.get(0));
                }
            }
        }

        return emails;
    }

    public static int[] makeKmpTable(String pattern){
        int patternLength = pattern.length();
        int[] kmpTable = new int[patternLength];

        int count = 0;
        for (int i = 1; i < patternLength; i++) {
            while (count > 0 && pattern.charAt(i) != pattern.charAt(count)) {
                count = kmpTable[count - 1];
            }

            if (pattern.charAt(i) == pattern.charAt(count)) {
                count += 1;
                kmpTable[i] = count;
            }
        }
        return kmpTable;
    }
    public static Boolean doKMP(String nickname, String pattern, int[] kmpTable){
        int nicknameLen = nickname.length();
        int patternLen = pattern.length();

        int count = 0;
        for (int i = 0; i < nicknameLen; i++) {
            while (count > 0 && nickname.charAt(i) != pattern.charAt(count)) {
                count = kmpTable[count - 1];
            }
            if (nickname.charAt(i) == pattern.charAt(count)) {
                if (count == patternLen - 1) {
                    return true;
                }
                count += 1;
            }
        }
        return false;
    }

    public static Set<String> findPattern(List<List<String>> forms) {
        Set<String> allPattern = new HashSet<>();
        Set<String> overlapPattern = new HashSet<>();
        for (List<String> form : forms) {
            if (form.get(1).length() == 1) {
                continue;
            }
            findPatternByWord(allPattern, overlapPattern, form.get(1));
        }

        return overlapPattern;
    }
    public static void findPatternByWord(Set<String> allPattern, Set<String> overlapPattern, String word) {
        Set<String> wordPatterns = new HashSet<>();
        for (int j = 0; j < word.length() - 1; j ++ ) {
            String subWord = word.substring(j, j + 2);
            wordPatterns.add(subWord);
        }
        addPatternToHashSet(allPattern, overlapPattern, wordPatterns);
    }

    private static void addPatternToHashSet(Set<String> allPattern, Set<String> overlapPattern, Set<String> wordPatterns) {
        for (String wordPattern : wordPatterns) {
            if (allPattern.contains(wordPattern)) {
                overlapPattern.add(wordPattern);
            }
            allPattern.add(wordPattern);
        }
    }

    private static void validate(final List<List<String>> forms) {
        validateInputData(forms);
        validateEmailAndNickname(forms);
    }

    private static void validateInputData(final List<List<String>> forms){
        Advice.checkInputDataLength(forms);
    }

    private static void validateEmailAndNickname(final List<List<String>> forms) {
        for (List<String> form : forms) {
            Advice.checkEmailValidation(form.get(0));
            Advice.checkEmailLength(form.get(0));
            Advice.checkNicknameLength(form.get(1));
            Advice.checkNicknameIsKorean(form.get(1));
        }
    }


    static class Advice{
        private static final char KOREAN_START = "가".charAt(0);
        private static final char KOREAN_FINAL = "힣".charAt(0);

        private Advice(){}
        public static void checkInputDataLength(final List<List<String>> forms) {
            if (forms.size() < 1 || forms.size() > 10000) {
                throw new IllegalArgumentException();
            }
        }

        public static void checkEmailValidation(final String email) {
            if (!EMAIL_PATTERN.equals(email.substring(email.indexOf("@") + 1))) {
                throw new IllegalArgumentException();
            }
        }

        public static void checkEmailLength(final String email) {
            if (email.length() < 11 || email.length() > 20) {
                throw new IllegalArgumentException();
            }
        }

        public static void checkNicknameLength(final String nickname) {
            if (nickname.length() < 1 || nickname.length() > 20) {
                throw new IllegalArgumentException();
            }
        }

        public static void checkNicknameIsKorean(final String nickname) {
            for (String str : nickname.split("")) {
                if (str.charAt(0) < KOREAN_START || str.charAt(0) > KOREAN_FINAL) {
                    throw new IllegalArgumentException();
                }
            }
        }

    }
}
