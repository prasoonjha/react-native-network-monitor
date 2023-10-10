import * as React from 'react';

import { StyleSheet, View, Text, Alert } from 'react-native';
import { multiply, reachable } from 'react-native-network-monitor';

export default function App() {
  const [result, setResult] = React.useState<number | undefined>();

  React.useEffect(() => {
    // reachable().then((res: boolean) => {
    //   console.log('res', res);
    // });
    multiply(3, 3).then(setResult);
    reachable().then((res) =>
      Alert.alert('network reachable', res ? 'reachable' : 'unreachable')
    );
  }, []);

  return (
    <View style={styles.container}>
      <Text>Result: {result}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
