# 접근성 터치 이식 가이드

이 문서는 `gemgemgen`의 접근성 터치 방식을 `deckphonephone`의 블루투스 설정 화면 자동화에 이식하기 위한 작업 메모입니다.

다른 코딩 에이전트가 이어받을 때는 이 문서를 먼저 읽고, 실제 수정 전에는 `docs/clean-architecture.mini.md`도 함께 확인하세요.

## 목표

- 현재 목표 화면: Android 블루투스 설정 화면
- 현재 문제: 저장된 블루투스 기기를 찾아도 터치가 동작하지 않거나 실패할 수 있음
- MVP 이후 목표: 다른 설정 화면에도 같은 방식의 접근성 터치 자동화를 확장

## 참고 프로젝트

| 프로젝트           | 경로                                                    | 참고할 내용                             |
| -------------- | ----------------------------------------------------- | ---------------------------------- |
| gemgemgen      | `C:\Use rs\joajo\AndroidStudioProjects\gemgemgen`     | 접근성 노드 클릭, 클릭 가능한 부모 탐색, 좌표 제스처 터치 |
| deckphonephone | `C:\Users\joajo\AndroidStudioProjects\deckphonephone` | 블루투스 설정 화면 자동화 대상 프로젝트             |

## gemgemgen의 핵심 방식

`gemgemgen`은 터치를 두 단계로 나눠 생각한다.

| 방식 | 설명 | 사용 예 |
|---|---|---|
| 접근성 노드 클릭 | 화면의 버튼/텍스트 노드를 찾고 `ACTION_CLICK` 실행 | Gemini/ChatGPT의 새 채팅, 보내기 버튼 |
| 좌표 제스처 터치 | 노드 클릭이 어렵거나 시스템 영역일 때 화면 좌표를 직접 탭 | DeX 최근 앱 버튼 |

### 접근성 서비스 설정

참고 파일:

- `gemgemgen/app/src/main/res/xml/gemini_accessibility_service.xml`

중요 설정:

```xml
android:accessibilityFlags="flagReportViewIds"
android:canPerformGestures="true"
android:canRetrieveWindowContent="true"
```

의미:

- `canRetrieveWindowContent`: 현재 화면의 접근성 노드를 읽을 수 있게 함
- `flagReportViewIds`: `viewIdResourceName`을 읽을 수 있게 함
- `canPerformGestures`: `dispatchGesture`로 좌표 터치를 할 수 있게 함

### 노드 클릭 방식

참고 파일:

- `gemgemgen/app/src/main/java/com/example/gemgemgen/automation/android/AccessibilityPromptAutomation.kt`

핵심 아이디어:

- 찾은 노드가 직접 클릭 가능하지 않을 수 있다.
- 그래서 자기 자신부터 부모 방향으로 올라가며 클릭 가능한 노드를 찾는다.
- 찾은 노드에 `AccessibilityNodeInfo.ACTION_CLICK`을 실행한다.

요약 흐름:

```text
대상 노드 찾기
→ 대상 노드가 클릭 가능하면 클릭
→ 아니면 부모 노드 중 클릭 가능한 노드 찾기
→ ACTION_CLICK 실행
→ 실패하면 재시도
```

### 좌표 터치 방식

참고 파일:

- `gemgemgen/app/src/main/java/com/example/gemgemgen/automation/android/GeminiAccessibilityService.kt`

핵심 아이디어:

- 접근성 노드 클릭이 안 되는 영역은 `dispatchGesture`로 직접 탭한다.
- `GestureDescription`과 `Path`를 만들어 특정 좌표를 누른다.
- 탭 성공/취소 콜백을 받아 다음 동작을 이어간다.

요약 흐름:

```text
탭할 좌표 계산
→ Path 생성
→ GestureDescription 생성
→ dispatchGesture 실행
→ onCompleted에서 다음 단계 진행
→ onCancelled에서 실패 처리
```

## deckphonephone의 현재 구조

현재 `deckphonephone`은 이미 접근성 자동화 구조가 있다.

| 파일 | 책임 |
|---|---|
| `deck/application/BluetoothDeviceActionPort.kt` | 유스케이스가 의존하는 포트 |
| `deck/platform/AndroidBluetoothDeviceActionAdapter.kt` | 접근성 서비스 확인, 블루투스 설정 화면 열기 |
| `deck/platform/BluetoothSettingsAutomationCoordinator.kt` | 현재 진행 중인 자동화 요청 보관 |
| `deck/platform/DeckBluetoothAccessibilityService.kt` | 설정 화면 접근성 노드를 읽고 클릭 실행 |
| `deck/platform/BluetoothSettingsNodeMatcher.kt` | 기기 이름/주소로 대상 후보 선택 |

현재 실행 흐름:

```text
블루투스 카드 실행
→ AndroidBluetoothDeviceActionAdapter.startBluetoothDeviceAction()
→ 접근성 서비스 활성 여부 확인
→ BluetoothSettingsAutomationCoordinator.tryStart()
→ Settings.ACTION_BLUETOOTH_SETTINGS 열기
→ DeckBluetoothAccessibilityService가 rootInActiveWindow 읽기
→ collectCandidates()로 클릭 후보 수집
→ BluetoothSettingsNodeMatcher.findTarget()으로 대상 찾기
→ ACTION_CLICK 실행
```

## 현재 실패 가능성이 큰 지점

### 1. View ID 보고 설정이 빠져 있음

현재 파일:

- `app/src/main/res/xml/deck_bluetooth_accessibility_service.xml`

현재는 `canRetrieveWindowContent`만 있다.

하지만 `DeckBluetoothAccessibilityService`는 아래처럼 View ID를 사용한다.

```kotlin
private const val DEVICE_TITLE_VIEW_ID = "android:id/title"
```

따라서 접근성 서비스 XML에 아래 설정이 필요할 가능성이 높다.

```xml
android:accessibilityFlags="flagReportViewIds"
```

### 2. 좌표 터치 권한이 없음

현재는 `dispatchGesture`를 쓰지 않으므로 아래 설정이 없다.

```xml
android:canPerformGestures="true"
```

노드 클릭 실패 시 좌표 fallback을 넣으려면 이 설정이 필요하다.

### 3. ACTION_CLICK만으로는 설정 화면 클릭이 실패할 수 있음

현재 클릭 위치:

- `app/src/main/java/com/example/deckphonephone/deck/platform/DeckBluetoothAccessibilityService.kt`

현재 방식:

```kotlin
match.candidate.value.performAction(AccessibilityNodeInfo.ACTION_CLICK)
```

문제 가능성:

- 설정 앱의 row가 실제로는 클릭 불가능할 수 있음
- 클릭 가능한 부모를 찾았더라도 제조사/OS별 설정 화면에서 `ACTION_CLICK`이 무시될 수 있음
- 사용자가 보는 터치 영역과 접근성 클릭 대상이 다를 수 있음

## 권장 이식 방향

큰 구조 변경 없이 아래 순서로 진행한다.

### 1단계: 접근성 서비스 XML 보강

수정 대상:

- `app/src/main/res/xml/deck_bluetooth_accessibility_service.xml`

추가 후보:

```xml
android:accessibilityFlags="flagReportViewIds"
android:canPerformGestures="true"
```

예상 효과:

- `viewIdResourceName` 기반 후보 판별이 더 안정적이 됨
- 노드 클릭 실패 시 좌표 터치 fallback을 사용할 수 있음

### 2단계: 현재 ACTION_CLICK 흐름 유지

현재 구조는 Clean Architecture 방향과 잘 맞는다.

- 유스케이스는 Android 접근성 API를 직접 모름
- Android 접근성 처리는 `deck/platform`에 있음
- 매칭 규칙은 `BluetoothSettingsNodeMatcher`로 테스트 가능하게 분리되어 있음

따라서 기존 흐름을 갈아엎지 말고, 클릭 실패 시 fallback만 추가한다.

### 3단계: 노드 클릭 실패 시 좌표 fallback 추가

권장 흐름:

```text
대상 후보 찾기
→ ACTION_CLICK 먼저 시도
→ 성공하면 기존처럼 markClicked()
→ 실패하면 후보 노드의 화면 bounds 확인
→ bounds 중앙 좌표를 dispatchGesture로 탭
→ 제스처 성공 시 markClicked()
→ 제스처 실패 시 실패 토스트
```

구현 후보 함수:

```kotlin
private fun clickCandidate(
    candidate: AccessibilityNodeInfo,
    onClicked: () -> Unit,
    onFailed: () -> Unit,
)
```

또는 이후 여러 화면에 재사용하려면 아래처럼 분리할 수 있다.

```kotlin
internal class AccessibilityClicker(
    private val service: AccessibilityService,
    private val handler: Handler,
)
```

다만 MVP에서는 새 파일을 만들지 않고 `DeckBluetoothAccessibilityService` 안에 private 함수로 먼저 넣어도 된다.

새 파일로 분리하는 기준:

- 블루투스 외 다른 설정 화면에서도 같은 클릭 fallback을 바로 쓸 계획이 확정된 경우
- 단위 테스트 가능한 클릭 정책을 따로 두고 싶은 경우
- `DeckBluetoothAccessibilityService`가 화면별 로직과 클릭 실행 책임을 너무 많이 함께 가지게 되는 경우

### 4단계: bounds 중앙 탭 방식

좌표 fallback은 후보 노드의 화면 영역을 이용한다.

예상 코드 형태:

```kotlin
private fun tapNodeCenter(
    node: AccessibilityNodeInfo,
    onCompleted: () -> Unit,
    onCancelled: () -> Unit,
): Boolean {
    val bounds = Rect()
    node.getBoundsInScreen(bounds)
    if (bounds.isEmpty) return false

    val path = Path().apply {
        moveTo(bounds.exactCenterX(), bounds.exactCenterY())
    }
    val gesture = GestureDescription.Builder()
        .addStroke(GestureDescription.StrokeDescription(path, 0L, TAP_DURATION_MS))
        .build()

    return dispatchGesture(
        gesture,
        object : AccessibilityService.GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                onCompleted()
            }

            override fun onCancelled(gestureDescription: GestureDescription?) {
                onCancelled()
            }
        },
        handler,
    )
}
```

주의:

- 위 코드는 방향 예시다. 실제 추가 시 import와 상수 위치를 정리해야 한다.
- `dispatchGesture()`는 즉시 성공/실패 결과를 반환하는 함수가 아니다.
- 실제 탭 완료 처리는 `onCompleted` 콜백에서 해야 한다.

## 다른 화면으로 확장할 때의 구조

MVP 이후 여러 설정 화면으로 확장할 경우 추천 구조:

| 역할 | 예시 이름 | 설명 |
|---|---|---|
| 클릭 실행 | `AccessibilityClicker` | `ACTION_CLICK` 후 실패 시 좌표 탭 |
| 노드 수집 | `AccessibilityNodeCollector` | 화면 노드 펼치기, 텍스트/설명/bounds 수집 |
| 화면별 매칭 | `BluetoothSettingsNodeMatcher` | 블루투스 화면에서 대상 기기 찾기 |
| 화면별 서비스 흐름 | `DeckBluetoothAccessibilityService` 또는 coordinator | 요청 상태, 재시도, 결과 안내 |

단, 지금 당장 모든 구조를 만들 필요는 없다.

첫 이식은 아래 정도가 적당하다.

```text
DeckBluetoothAccessibilityService 안에 private 좌표 탭 fallback 추가
→ XML 설정 보강
→ 기존 matcher 테스트 유지
→ 실제 기기에서 블루투스 화면 클릭 확인
```

## 예상 수정 파일

필수에 가까운 수정:

| 파일 | 변경 내용 |
|---|---|
| `app/src/main/res/xml/deck_bluetooth_accessibility_service.xml` | `flagReportViewIds`, `canPerformGestures` 추가 |
| `app/src/main/java/com/example/deckphonephone/deck/platform/DeckBluetoothAccessibilityService.kt` | `ACTION_CLICK` 실패 시 `dispatchGesture` fallback 추가 |

테스트 추가 후보:

| 파일 | 변경 내용 |
|---|---|
| `app/src/test/java/com/example/deckphonephone/deck/platform/BluetoothSettingsNodeMatcherTest.kt` | matcher 변경이 있을 때만 추가 |

신규 파일 후보:

| 파일 | 만들 조건 |
|---|---|
| `app/src/main/java/com/example/deckphonephone/deck/platform/AccessibilityClicker.kt` | 클릭 fallback을 다른 화면에서도 재사용할 계획이 확정된 경우 |

## 검증 방법

### 자동 테스트

기존 matcher 테스트:

```powershell
.\gradlew.bat :app:test --tests "*BluetoothSettingsNodeMatcherTest"
```

전체 단위 테스트:

```powershell
.\gradlew.bat :app:test
```

### 실제 기기 확인

1. 앱 설치
2. 접근성 서비스 활성화
3. 오버레이 또는 카드에서 블루투스 기기 카드 실행
4. Android 블루투스 설정 화면이 열리는지 확인
5. 저장된 기기 row가 자동으로 눌리는지 확인
6. 연결 상태가 바뀌는지 확인
7. 실패 토스트가 뜨는 경우, 기기 이름/주소가 화면에 실제로 보이는지 확인

## 구현 시 주의점

- 접근성 API는 Android 프레임워크 세부사항이므로 `deck/application`이나 `deck/domain`으로 가져오면 안 된다.
- 블루투스 설정 화면의 UI는 제조사와 Android 버전에 따라 다를 수 있다.
- 좌표 탭은 마지막 fallback으로만 쓰는 것이 안전하다.
- 기기 이름만으로 매칭하면 같은 이름의 기기가 있을 때 잘못 누를 수 있다.
- 가능하면 주소 매칭을 우선하고, 주소가 화면에 없을 때만 이름 매칭을 사용한다.
- 새 라이브러리는 필요 없다. Android 접근성 표준 API만으로 충분하다.

## 추천 작업 순서

1. `deck_bluetooth_accessibility_service.xml`에 `flagReportViewIds`, `canPerformGestures` 추가
2. `DeckBluetoothAccessibilityService`에서 클릭 실행 부분을 private 함수로 감싸기
3. 해당 함수에서 `ACTION_CLICK` 먼저 시도
4. 실패하면 노드 bounds 중앙을 `dispatchGesture`로 탭
5. 제스처 완료 콜백에서 기존 `markClicked()` 흐름 실행
6. `BluetoothSettingsNodeMatcherTest` 실행
7. 실제 Android 기기에서 블루투스 카드 실행 확인

## 짧은 결론

`gemgemgen`의 방식을 `deckphonephone`에 이식할 수 있다.

가장 작은 안전한 변경은 다음 두 가지다.

1. 접근성 서비스 설정에 View ID 보고와 제스처 수행 권한 추가
2. 현재 노드 클릭이 실패할 때만 좌표 탭 fallback 추가

이렇게 하면 현재 블루투스 설정 MVP를 크게 흔들지 않으면서, 이후 다른 설정 화면 자동화에도 재사용 가능한 기반을 만들 수 있다.
