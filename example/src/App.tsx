import { Button, SafeAreaView, StyleSheet, Text, View } from 'react-native';
import { toast } from 'react-native-nitro-simple-toast';

export default function App() {
  return (
    <SafeAreaView style={styles.container}>
      <Text style={styles.title}>Nitro Simple Toast</Text>

      <View style={styles.buttonGroup}>
        <Button
          title="Done Toast"
          onPress={() =>
            toast({
              title: 'Success',
              message: 'Operation completed',
              preset: 'done',
              haptic: 'success',
            })
          }
        />

        <Button
          title="Error Toast"
          onPress={() =>
            toast({
              title: 'Error',
              message: 'Something went wrong',
              preset: 'error',
              haptic: 'error',
            })
          }
        />

        <Button
          title="Plain Toast"
          onPress={() =>
            toast({
              title: 'Hello',
              preset: 'none',
              haptic: 'none',
            })
          }
        />

        <Button
          title="Bottom Toast (iOS)"
          onPress={() =>
            toast({
              title: 'From Bottom',
              message: 'iOS only feature',
              preset: 'done',
              from: 'bottom',
            })
          }
        />

        <Button
          title="Long Duration"
          onPress={() =>
            toast({
              title: 'Persistent',
              message: 'This stays longer',
              preset: 'done',
              duration: 5,
              shouldDismissByDrag: true,
            })
          }
        />

        <Button
          title="Light Theme Toast"
          onPress={() =>
            toast({
              title: 'Light Mode',
              message: 'Forced light theme',
              preset: 'done',
              theme: 'light',
            })
          }
        />

        <Button
          title="Dark Theme Toast"
          onPress={() =>
            toast({
              title: 'Dark Mode',
              message: 'Forced dark theme',
              preset: 'done',
              theme: 'dark',
            })
          }
        />
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#f5f5f5',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 32,
  },
  buttonGroup: {
    gap: 12,
    width: '80%',
  },
});
